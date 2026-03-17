import { configureStore } from '@reduxjs/toolkit';
import tradeReducer from './tradeSlice';
import orderBookReducer from './orderBookSlice';;

export const store = configureStore({
  reducer: {
    trades: tradeReducer,
    orderBook: orderBookReducer,
  },
});