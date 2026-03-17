import { createSlice } from '@reduxjs/toolkit';

const tradeSlice = createSlice({
  name: 'trades',
  initialState: {
    tradesList: [],
  },
  reducers: {
    
    addTrade: (state, action) => {
      state.tradesList.unshift(action.payload);
      
      if (state.tradesList.length > 50) {
        state.tradesList.pop();
      }
    },
    
    setInitialTrades: (state, action) => {
      state.tradesList = action.payload;
    },
  },
});

export const { addTrade, setInitialTrades } = tradeSlice.actions; 
export default tradeSlice.reducer;