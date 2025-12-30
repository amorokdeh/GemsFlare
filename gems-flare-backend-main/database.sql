CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.bill_address (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    userid UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    housenumber VARCHAR(255) NOT NULL,
    zipcode VARCHAR(255) NOT NULL,
    county VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL
);

INSERT INTO public.bill_address (id, userid, name, lastname, street, housenumber, zipcode, county, country) VALUES
    ('04a9bc40-b4fb-4118-963d-0423f1b7520c','1ee6f0f9-1918-4036-99f2-0a27c924398b','Amr','Okdeh','Kalischer Str.','7','28237','Bremen','Germany');

CREATE TABLE IF NOT EXISTS public.category (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL
);

INSERT INTO public.category (id, name) VALUES
    ('0b28a2ff-2549-45c8-92c9-3a9c36ffa28e','test');

CREATE TABLE IF NOT EXISTS public.checkout (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    userid UUID NOT NULL,
    items TEXT,
    sum NUMERIC(38,2),
    paid BOOLEAN DEFAULT false,
    date TIMESTAMP,
    number VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS public.delivery_address (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    userid UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    housenumber VARCHAR(255) NOT NULL,
    zipcode VARCHAR(255) NOT NULL,
    county VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL
);

INSERT INTO public.delivery_address (id, userid, name, lastname, street, housenumber, zipcode, county, country) VALUES
    ('f05c06a1-6f6e-4a67-891c-f03f84b33e82','1ee6f0f9-1918-4036-99f2-0a27c924398b','Amr','Okdeh','Erkelenzer Str.','9','28327','Bremen','Germany');

CREATE TABLE IF NOT EXISTS public.invoice (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    number VARCHAR(255),
    issuedate DATE,
    orderdate DATE,
    ordernumber VARCHAR(255),
    billaddress TEXT,
    shippingaddress TEXT,
    items TEXT,
    totalamount NUMERIC(38,2),
    totalamountwithouttax NUMERIC(38,2),
    tax VARCHAR(255),
    payment VARCHAR(255)
);

INSERT INTO public.invoice (id, number, issuedate, orderdate, ordernumber, billaddress, shippingaddress, items, totalamount, totalamountwithouttax, tax, payment) VALUES
    (
        '267e7600-9720-4925-acf8-b1bd6ef548e9',
        'GEMSFLARE-2025-04-29-000002',
        '2025-04-29',
        '2025-04-29',
        '783-928-270-257',
        '{"name":"Amr","lastname":"Okdeh","street":"Kalischer Str.","housenumber":"7","zipcode":"28237","county":"Bremen","country":"Germany"}',
        '{"name":"Amr","lastname":"Okdeh","street":"Erkelenzer Str.","housenumber":"9","zipcode":"28327","county":"Bremen","country":"Germany"}',
        '[{"id":"8b63c684-1678-48f6-99f9-07a7497df6b1","name":"newTest","number":"442-227-914-737","description":"descritipn test test ...","category":"test","color_groups":["test1","test2"],"price":0.01,"amount":2,"img_src":"http://localhost:8080/uploads/items/template/IMG1.png","object_src":"http://localhost:8080/uploads/items/template/OBJ1.obj"},{"id":"0ea2802a-8b1a-46a2-b044-0132dbe3502c","name":"test","number":"299-782-474-326","description":"","category":"test","color_groups":["test"],"price":0.01,"amount":5,"img_src":"http://localhost:8080/uploads/items/template/IMG1.png","object_src":"http://localhost:8080/uploads/items/template/OBJ1.obj"}]',
        0.02,
        0.02,
        '19%',
        'PayPall'
    ),
    (
        '5332ffb5-52b9-41e6-a806-eeed5df7997d',
        'GEMSFLARE-2025-04-29-000001',
        '2025-04-29',
        '2025-04-29',
        '129-305-920-91',
        '{"name":"Amr","lastname":"Okdeh","street":"Kalischer Str.","housenumber":"7","zipcode":"28237","county":"Bremen","country":"Germany"}',
        '{"name":"Amr","lastname":"Okdeh","street":"Erkelenzer Str.","housenumber":"9","zipcode":"28327","county":"Bremen","country":"Germany"}',
        '[{"id":"0ea2802a-8b1a-46a2-b044-0132dbe3502c","name":"test","number":"299-782-474-326","description":"","category":"test","color_groups":["test"],"price":0.01,"amount":1,"img_src":"http://localhost:8080/uploads/items/template/IMG1.png","object_src":"http://localhost:8080/uploads/items/template/OBJ1.obj"}]',
        0.01,
        0.01,
        '19%',
        'PayPall'
    );

CREATE TABLE IF NOT EXISTS public.invoice_counter (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    date DATE,
    counter INTEGER
);

INSERT INTO public.invoice_counter (id, date, counter) VALUES
    (
        '4ded9688-7a33-4787-8dfe-aeba09022b63',
        '2025-04-29',
        2
    );

CREATE TABLE IF NOT EXISTS public.item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    price NUMERIC(38,2) NOT NULL DEFAULT 0.00,
    amount INTEGER NOT NULL DEFAULT 0,
    number VARCHAR(255) NOT NULL,
    color_groups VARCHAR(255) NOT NULL,
    img_src VARCHAR(255) NOT NULL,
    object_src VARCHAR(255),
    description TEXT
);

INSERT INTO public.item (
    id, name, category, price, amount, number, color_groups, img_src, object_src, description
) VALUES (
        '72298ae5-a062-4229-8a4e-91554fd2a558',
        'test',
        'test',
        1.00,
        8,
        '138-557-223-525',
        '["rrr"]',
        'http://localhost:8080/uploads/items/template/IMG1.png',
        'http://localhost:8080/uploads/items/template/OBJ1.obj',
        NULL
    );

CREATE TABLE IF NOT EXISTS public."order" (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    number VARCHAR(255) NOT NULL,
    items TEXT,
    sum NUMERIC(38,2),
    userid UUID NOT NULL,
    date TIMESTAMP,
    state VARCHAR(255) DEFAULT 'In Progress',
    transaction VARCHAR(255)
);

INSERT INTO public."order" (
    id, number, items, sum, userid, date, state, transaction
) VALUES
      (
          '0c26f657-c468-47d6-b40c-6f8e762642a8',
          '129-305-920-91',
          '[{"id":"0ea2802a-8b1a-46a2-b044-0132dbe3502c","name":"test","number":"299-782-474-326","description":"","category":"test","color_groups":["test"],"price":0.01,"amount":1,"img_src":"http://localhost:8080/uploads/items/template/IMG1.png","object_src":"http://localhost:8080/uploads/items/template/OBJ1.obj"}]',
          0.01,
          '1ee6f0f9-1918-4036-99f2-0a27c924398b',
          '2025-04-29 17:52:17.991',
          'Canceled',
          '8SS8552049273511V'
      ),
      (
          '8c2abfe6-d519-46e4-a454-d5ef49637593',
          '783-928-270-257',
          '[{"id":"8b63c684-1678-48f6-99f9-07a7497df6b1","name":"newTest","number":"442-227-914-737","description":"descritipn test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test test","category":"test","color_groups":["test1","test2"],"price":0.01,"amount":2,"img_src":"http://localhost:8080/uploads/items/template/IMG1.png","object_src":"http://localhost:8080/uploads/items/template/OBJ1.obj"},{"id":"0ea2802a-8b1a-46a2-b044-0132dbe3502c","name":"test","number":"299-782-474-326","description":"","category":"test","color_groups":["test"],"price":0.01,"amount":5,"img_src":"http://localhost:8080/uploads/items/template/IMG1.png","object_src":"http://localhost:8080/uploads/items/template/OBJ1.obj"}]',
          0.02,
          '1ee6f0f9-1918-4036-99f2-0a27c924398b',
          '2025-04-29 20:15:40.913',
          'Canceled',
          '9RG68148ED720041X'
      );

CREATE TABLE IF NOT EXISTS permission (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    route VARCHAR(255) NOT NULL,
    users TEXT NOT NULL,
    admins TEXT NOT NULL
);

INSERT INTO permission (id, route, users, admins) VALUES
    ('013fb7f5-e3ca-44d3-b93d-85234daad905','/item/590-583-9-438','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('0f3e1f07-6b83-46c6-8b0d-b45ab8c30e00','/item/794-376-128-883','["343b899a-cbb3-4a21-8e09-2532371b068b"]','["343b899a-cbb3-4a21-8e09-2532371b068b"]'),
    ('185e7909-25b5-48a8-8328-2f8e34fd88c2','/item/951-338-753-458','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('2e767523-4c20-4501-b773-8c73c6d5bb9a','/changePasswordByAdmin','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('321f2c09-b23c-4627-8bd9-8c1c577665f5','/getAllDeliveryAddresses','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","228d5a38-fa9f-4fb3-9c58-fd9a86734a69"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('446602c6-be5e-4b69-b766-33e563290513','/item/299-782-474-326','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('45e17ed7-f016-4f2e-ba63-0fc72642e636','/item/274-529-1-965','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('4dd9530f-3279-4ea8-9a55-01daaee14780','/item/479-28-167-61','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('4f8f8e06-6fd6-493a-a0eb-8ed3bb4ffcd8','/editUserProfileByAdmin','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","228d5a38-fa9f-4fb3-9c58-fd9a86734a69"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('60b462af-968c-4f5a-a204-ff8bebb57335','/item/497-851-913-835','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('73ffe3ba-5084-47a2-aa9d-7b96a94df4e5','/item/614-846-495-880','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('746994a8-89c8-4d15-abc3-67a86778b877','/getAllPermissions','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","228d5a38-fa9f-4fb3-9c58-fd9a86734a69"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('7f80d3ab-c01a-47fe-b12b-9c5f064d9f7c','/item/473-742-363-514','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('853bb7d3-0fab-42ed-ad85-0a4b79dfd614','/getAllBillAddresses','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","228d5a38-fa9f-4fb3-9c58-fd9a86734a69"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('8ca800d4-f249-4047-9436-99d1b63e6e88','/removeDeliveryAddressFromUserByAdmin','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('a35c3b35-1fae-47fc-9e86-b53c272c9da6','/addItem','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","228d5a38-fa9f-4fb3-9c58-fd9a86734a69","07b0da47-d041-41ab-b9af-cd96485077a7","343b899a-cbb3-4a21-8e09-2532371b068b","1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('a9b85ee5-b903-41ac-97e3-c75a130dfe85','/deleteUserByAdmin','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('a9ca0608-1ede-4c3e-ac20-96671cd212b5','/getAllUsers','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","228d5a38-fa9f-4fb3-9c58-fd9a86734a69"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('b1797d4b-6a0d-439e-94b4-60c0db3330a3','/addDeliveryAddressToUserByAdmin','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('b7969f21-52f1-44d5-b9c1-5d22ae1a7af0','/item/953-281-27-837','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('bd70bda5-8a5b-475c-bf2c-9f44447cd5ac','/item/643-197-985-60','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('bdddfd83-f5de-4454-8b59-01618eecaeab','/item/409-506-524-331','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('c122f14e-89cc-49dd-ac93-a53d9d5989f3','/item/155-376-449-67','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('c540f306-a641-4a32-92f8-5f9a199376f0','/order','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('d16f4272-94b8-401c-a949-d7f9d40aaa2b','/item/442-227-914-737','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('d49b724a-547e-4883-8377-b2b634b820b0','/checkout','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('d89c72d0-5dfc-4e98-bd98-b9bdae9c462f','/getUserPermissions','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3","228d5a38-fa9f-4fb3-9c58-fd9a86734a69"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('e31c5b12-f944-4259-9717-9ced2d7a10c4','/item/138-557-223-525','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]','["1ee6f0f9-1918-4036-99f2-0a27c924398b"]'),
    ('e54995e1-2de0-4dff-9044-615aa43ec234','/addCategory','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('e7da1d98-e8d1-4a09-a8d3-292ee443cc6b','/addBillAddressToUserByAdmin','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('ead22899-9777-47dd-b198-e0882086e382','/testDb','["228d5a38-fa9f-4fb3-9c58-fd9a86734a69"]','["228d5a38-fa9f-4fb3-9c58-fd9a86734a69"]'),
    ('eec10107-ee94-4580-ad9b-d64c13275229','/deleteCategory','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]'),
    ('f9ca4618-012c-414b-8c65-44216ea7e460','/removeBillAddressFromUserByAdmin','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]','["687ee0d9-4c80-402c-93f2-f9c8ab5479f3"]');

CREATE TABLE IF NOT EXISTS "user" (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL DEFAULT 'user',
    address VARCHAR(255),
    bill_address_id UUID UNIQUE,
    delivery_address_id UUID UNIQUE,
    email VARCHAR(255) NOT NULL,
    telephone VARCHAR(255),
    CONSTRAINT fkgql2kgooe1b84a01qblw5e5bt FOREIGN KEY (delivery_address_id) REFERENCES delivery_address(id),
    CONSTRAINT fkr66teh5ovo19p5qruc94fwwgv FOREIGN KEY (bill_address_id) REFERENCES bill_address(id),
    CONSTRAINT uk9anr5vhyl4xljgi0uxc6j85ul UNIQUE(delivery_address_id),
    CONSTRAINT ukryhh45d64y0qm65w10t7pgh1i UNIQUE(bill_address_id)
);

INSERT INTO "user" (id, username, password, name, lastname, role, address, bill_address_id, delivery_address_id, email, telephone) VALUES
    ('687ee0d9-4c80-402c-93f2-f9c8ab5479f3','admin','admin','admin','admin','admin','null',NULL,NULL,'admin@gemsflare.com','+491742356277');