import { createSlice } from '@reduxjs/toolkit';

const orderBookSlice = createSlice({
  name: 'orderBook',
  initialState: {
    bids: [], 
    asks: [], 
  },
  reducers: {
    setOrderBook: (state, action) => {
      state.bids = action.payload.bids || [];
      state.asks = action.payload.asks || [];
    },
  },
});

export const { setOrderBook } = orderBookSlice.actions;
export default orderBookSlice.reducer;