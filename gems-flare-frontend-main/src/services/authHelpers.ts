export const getAuthToken = () => {
    const user = localStorage.getItem("user");
    if (user) {
      const { token } = JSON.parse(user);
      return token;
    }
    return null;
  };
  