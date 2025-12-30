package com.gemsflare.gemsflare.item.service;

import com.gemsflare.gemsflare.item.jpa.CategoryEntity;
import com.gemsflare.gemsflare.item.jpa.ItemEntity;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import com.gemsflare.gemsflare.item.repository.CategoryRepository;
import com.gemsflare.gemsflare.item.repository.ItemRepository;
import com.gemsflare.gemsflare.permission.service.PermissionService;
import com.gemsflare.gemsflare.security.JwtUtil;
import com.gemsflare.gemsflare.storage.service.StorageFolderService;
import com.gemsflare.gemsflare.storage.service.StorageImageService;
import com.gemsflare.gemsflare.storage.service.StorageObjectService;
import com.gemsflare.gemsflare.user.jpa.UserEntity;
import com.gemsflare.gemsflare.user.repository.UserRepository;
import com.gemsflare.gemsflare.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ItemService {

    @Value("${storage.base-url}")
    private String baseStorageUrl;

    @Value("${storage.folder-prefix}")
    private String folderPrefix;

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageFolderService folderService;
    @Autowired
    private StorageImageService imageService;
    @Autowired
    private StorageObjectService objectService;
    @Autowired
    private UserService userService;

    private String buildFileUrl(String itemNumber, String fileName) {
        return String.format("%s/%s/%s", baseStorageUrl, itemNumber, fileName);
    }

    public ResponseEntity<?> getAllItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemEntity> itemsPage = itemRepository.findAll(pageable);

        if (itemsPage.isEmpty()) {
            return ResponseEntity.status(404).body("No items found");
        }

        return ResponseEntity.ok(itemsPage);
    }

    public ResponseEntity<?> getAllUserItems(HttpServletRequest request, int page, int size) {
        UUID requesterId = userService.getUserIdFromRequest(request);
        Pageable pageable = PageRequest.of(page, size);
        ResponseEntity<?> response = permissionService.getUserItemsPermissions(request, requesterId, null);

        if (response.getStatusCode().isError() || response.getBody() == null) {
            return ResponseEntity.status(403).body("Access denied or no permissions found");
        }

        List<String> itemNumbers;
        try {
            itemNumbers = (List<String>) response.getBody();
        } catch (ClassCastException e) {
            return ResponseEntity.status(500).body("Unexpected response format");
        }

        Page<ItemEntity> itemsPage = itemRepository.findByNumberIn(itemNumbers, pageable);

        return ResponseEntity.ok(itemsPage);
    }

    public ResponseEntity<?> getItemByItemNumber(String itemNumber) {
        Optional<ItemEntity> itemOpt = itemRepository.findByNumber(itemNumber);
        if (itemOpt.isPresent()) {
            return ResponseEntity.ok(itemOpt.get());
        } else {
            return ResponseEntity.status(404).body("Item not found with number: " + itemNumber);
        }
    }

    public ResponseEntity<?> getItemsByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemEntity> itemsPage = itemRepository.findByNameContainingIgnoreCase(name, pageable);

        if (itemsPage.isEmpty()) {
            return ResponseEntity.status(404).body("No items found with name or subname: " + name);
        }

        return ResponseEntity.ok(itemsPage);
    }

    public ResponseEntity<?> getItemsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ItemEntity> itemsPage = itemRepository.findByCategoryContainingIgnoreCase(category, pageable);

        if (itemsPage.isEmpty()) {
            return ResponseEntity.status(404).body("No items found with category: " + category);
        }

        return ResponseEntity.ok(itemsPage);
    }

    public ResponseEntity<?> addItem(HttpServletRequest request, String name, String description, String category,
                                     List<String> colorGroups, BigDecimal price, Integer amount,
                                     MultipartFile image, MultipartFile object) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, "/addItem")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }

        if (name == null || category == null || price == null || amount == null || colorGroups == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields");
        }

        CategoryEntity categoryEntity = categoryRepository.findByName(category)
                .orElseGet(() -> {
                    CategoryEntity newCategory = new CategoryEntity();
                    newCategory.setName(category);
                    return categoryRepository.save(newCategory);
                });

        String newItemNumber = generateRandomNumber();

        ItemEntity newItem = new ItemEntity();
        newItem.setName(name);
        if(description != null) {
            newItem.setDescription(description);
        }
        newItem.setCategory(categoryEntity.getName());
        newItem.setColor_groups(colorGroups);
        newItem.setPrice(price);
        newItem.setAmount(amount);
        newItem.setNumber(newItemNumber);

        permissionService.addPermissionItem(request,newItem.getNumber());

        if (image == null) {
            newItem.setImg_src(baseStorageUrl + "template/IMG1.png");
        } else {

            String imageFileName = imageService.generateRandomFileName(image);
            imageService.uploadImageToFolder(request, image, newItemNumber, imageFileName);

            String imageUrl = baseStorageUrl + newItemNumber + "/" + imageFileName;
            newItem.setImg_src(imageUrl);
        }

        if (object == null) {
            newItem.setObject_src(baseStorageUrl + "template/OBJ1.obj");
        } else {
            String objectFileName = objectService.generateRandomFileName(object);
            objectService.uploadObjectToFolder(request, object, newItemNumber, objectFileName);

            String objectUrl = baseStorageUrl + newItemNumber + "/" + objectFileName;
            newItem.setObject_src(objectUrl);
        }

        ItemEntity savedItem = itemRepository.save(newItem);

        return ResponseEntity.ok(savedItem);
    }

    public ResponseEntity<?> deleteItem(HttpServletRequest request, String itemNumber, String password) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String route = "/item/" + itemNumber;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, route)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }

        Optional<ItemEntity> itemOpt = itemRepository.findByNumber(itemNumber);

        if (!itemOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found with number: " + itemNumber);
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);
        Optional<UserEntity> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        UserEntity user = optionalUser.get();

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Incorrect password");
        }

        ItemEntity itemToDelete = itemOpt.get();
        String category = itemToDelete.getCategory();
        folderService.deleteItemFolder(request, itemNumber);
        itemRepository.delete(itemToDelete);
        permissionService.deletePermission(request, route, password);

        boolean hasOtherItems = itemRepository.existsByCategory(category);
        if (!hasOtherItems) {
            categoryRepository.findByName(category).ifPresent(categoryRepository::delete);
        }

        return ResponseEntity.ok("Item with number " + itemNumber + " has been deleted successfully");
    }

    public ResponseEntity<?> editItem(HttpServletRequest request, String itemNumber, String name,
                                      String description, String category, List<String> colorGroups,
                                      BigDecimal price, Integer amount, MultipartFile image,
                                      MultipartFile object) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String route = "/item/" + itemNumber;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, route)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }

        Optional<ItemEntity> itemOpt = itemRepository.findByNumber(itemNumber);

        if (!itemOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found with number: " + itemNumber);
        }

        ItemEntity itemToEdit = itemOpt.get();
        String oldCategory = itemToEdit.getCategory();

        if (name != null && !name.trim().isEmpty()) {
            itemToEdit.setName(name);
        }
        if (description != null && !description.trim().isEmpty()) {
            itemToEdit.setDescription(description);
        }
        if (colorGroups != null && !colorGroups.isEmpty()) {
            itemToEdit.setColor_groups(colorGroups);
        }
        if (price != null) {
            itemToEdit.setPrice(price);
        }
        if (amount != null) {
            itemToEdit.setAmount(amount);
        }

        if (category != null && !category.trim().isEmpty() && !category.equals(oldCategory)) {
            Optional<CategoryEntity> newCategoryOpt = categoryRepository.findByName(category);
            if (newCategoryOpt.isEmpty()) {
                CategoryEntity newCategory = new CategoryEntity();
                newCategory.setName(category);
                categoryRepository.save(newCategory);
            }

            itemToEdit.setCategory(category);
        }

        if (image != null) {
            String imageFileName = imageService.generateRandomFileName(image);
            String imageUrl = baseStorageUrl + itemNumber + "/" + imageFileName;

            if(itemToEdit.getImg_src().equals(baseStorageUrl + "template/IMG1.png")){
                imageService.uploadImageToFolder(request, image, itemNumber, imageFileName);
                itemToEdit.setImg_src(imageUrl);
            }
            else {
                imageService.editImage(request, image, itemNumber, itemToEdit.getImg_src() , imageFileName);
                itemToEdit.setImg_src(imageUrl);
            }
        }
        if (object != null) {
            String objectFileName = objectService.generateRandomFileName(object);
            String objectUrl = baseStorageUrl + itemNumber + "/" + objectFileName;

            if(itemToEdit.getObject_src().equals(baseStorageUrl + "template/OBJ1.obj")){
                objectService.uploadObjectToFolder(request, object, itemNumber, objectFileName);
                itemToEdit.setObject_src(objectUrl);
            }
            else {
                objectService.editObject(request, object, itemNumber, itemToEdit.getObject_src() , objectFileName);
                itemToEdit.setObject_src(objectUrl);
            }
        }

        ItemEntity savedItem = itemRepository.save(itemToEdit);

        if (!oldCategory.equals(itemToEdit.getCategory())) {
            boolean hasOtherItems = itemRepository.existsByCategory(oldCategory);
            if (!hasOtherItems) {
                Optional<CategoryEntity> oldCategoryEntity = categoryRepository.findByName(oldCategory);
                oldCategoryEntity.ifPresent(categoryRepository::delete);
            }
        }

        return ResponseEntity.ok(savedItem);
    }

    public ResponseEntity<?> deleteImageFromItem(HttpServletRequest request, String itemNumber) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String route = "/item/" + itemNumber;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, route)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }

        Optional<ItemEntity> itemOpt = itemRepository.findByNumber(itemNumber);

        if (!itemOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found with number: " + itemNumber);
        }

        ItemEntity item = itemOpt.get();
        String imageUrl = item.getImg_src();
        if(imageUrl.equals(baseStorageUrl + "template/IMG1.png")){
            return ResponseEntity.status(404).body("Item do not have any image to removes");
        }
        String imageToDelete = imageService.getFileNameFromLink(imageUrl);
        imageService.deleteImage(request, itemNumber, imageToDelete);

        item.setImg_src(baseStorageUrl + "template/IMG1.png");
        itemRepository.save(item);

        return ResponseEntity.ok("Image for item " + itemNumber + " has been deleted successfully");
    }

    public ResponseEntity<?> deleteObjectFromItem(HttpServletRequest request, String itemNumber) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String route = "/item/" + itemNumber;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, route)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }

        Optional<ItemEntity> itemOpt = itemRepository.findByNumber(itemNumber);

        if (!itemOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found with number: " + itemNumber);
        }

        ItemEntity item = itemOpt.get();
        String objectUrl = item.getObject_src();
        String objectToDelete = objectService.getFileNameFromLink(objectUrl);
        objectService.deleteObject(request, itemNumber, objectToDelete);

        item.setObject_src(baseStorageUrl + "template/OBJ1.obj");
        itemRepository.save(item);

        return ResponseEntity.ok("Object for item " + itemNumber + " has been deleted successfully");
    }

    public ResponseEntity<?> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            return ResponseEntity.status(404).body("No items found");
        }

        return ResponseEntity.ok(categories);
    }

    public ResponseEntity<?> addCategory(HttpServletRequest request, String name) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, "/addCategory")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }

        if (name == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields");
        }

        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setName(name);

        CategoryEntity savedCategory = categoryRepository.save(newCategory);

        return ResponseEntity.ok(savedCategory);
    }

    public ResponseEntity<?> deleteCategory(HttpServletRequest request, String name) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        if (!permissionService.hasPermission(request, "/deleteCategory")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Permission required");
        }

        Optional<CategoryEntity> categoryOpt = categoryRepository.findByName(name);

        if (!categoryOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found with name: " + name);
        }

        CategoryEntity categoryToDelete = categoryOpt.get();
        categoryRepository.delete(categoryToDelete);

        return ResponseEntity.ok("Category with name " + name + " has been deleted successfully");
    }

    public ResponseEntity<?> changeItemAmount(HttpServletRequest request, String itemNumber, Integer amount) {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token");
        }

        Optional<ItemEntity> itemOpt = itemRepository.findByNumber(itemNumber);
        if (itemOpt.isPresent()) {
            ItemEntity item = itemOpt.get();
            item.setAmount(item.getAmount() + amount);
            itemRepository.save(item);
            return ResponseEntity.ok("Item with number " + itemNumber + " has the amount: " + item.getAmount());
        } else {
            return ResponseEntity.status(404).body("Item not found with number: " + itemNumber);
        }
    }

    private String generateRandomNumber() {
        long randomNum = (long) (Math.floor(Math.random() * (900000000000L)) + 100000000000L);
        return String.format("%d-%d-%d-%d", randomNum / 1000000000, (randomNum / 1000000) % 1000, (randomNum / 1000) % 1000, randomNum % 1000);
    }

    public ItemDTO getItemByNumber(String itemNumber) {
        Optional<ItemEntity> itemOpt = itemRepository.findByNumber(itemNumber);
        return itemOpt.map(this::convertToDTO).orElse(null);
    }

    private ItemDTO convertToDTO(ItemEntity itemEntity) {
        ItemDTO dto = new ItemDTO();
        dto.setId(itemEntity.getId());
        dto.setName(itemEntity.getName());
        dto.setNumber(itemEntity.getNumber());
        dto.setDescription(itemEntity.getDescription());
        dto.setCategory(itemEntity.getCategory());
        dto.setColor_groups(itemEntity.getColor_groups());
        dto.setPrice(itemEntity.getPrice());
        dto.setAmount(itemEntity.getAmount());
        dto.setImg_src(itemEntity.getImg_src());
        dto.setObject_src(itemEntity.getObject_src());
        return dto;
    }
}
