import { configureStore } from "@reduxjs/toolkit";
import { ProductReducer } from "./ProductReducer";
import { errorReducer } from "./errorReducer";
import { cartReducer } from "./cartReducer";
import { authReducer } from "./authReducer";
import { paymentMethodReducer } from "./paymentMethodReducer";
import { adminReducer } from "./adminReducer";
import { orderReducer } from "./orderReducer";
import { sellerReducer } from "./sellerReducer";

const cartItems = localStorage.getItem("cartItems")
  ? JSON.parse(localStorage.getItem("cartItems"))
  : [];
const selectedUserCheckoutAddress = localStorage.getItem("CHECKOUT_ADDRESS")
  ? JSON.parse(localStorage.getItem("cartItems"))
  : [];
const user = localStorage.getItem("auth")
  ? JSON.parse(localStorage.getItem("auth"))
  : null;

const initialState = {
  auth: { user: user, selectedUserCheckoutAddress },
  carts: { cart: cartItems },
};
export const store = configureStore({
  reducer: {
    products: ProductReducer,
    errors: errorReducer,
    carts: cartReducer,
    auth: authReducer,
    payment: paymentMethodReducer,
    admin: adminReducer,
    order: orderReducer,
    sellers: sellerReducer,
  },
  preloadedState: initialState,
});
export default store;
