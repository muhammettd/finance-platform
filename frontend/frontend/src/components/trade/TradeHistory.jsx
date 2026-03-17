import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import useWebSocket from '../../hooks/useWebSocket';
import { setInitialTrades } from '../../store/tradeSlice'; 

const TradeHistory = ({ symbolPair = 'BTC_USDT' }) => {
    
    useWebSocket(symbolPair);
    
    const dispatch = useDispatch();
    const trades = useSelector((state) => state.trades?.tradesList || []);

    
    useEffect(() => {
        const fetchHistory = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/v1/orders/history/${symbolPair}`);
                if (response.ok) {
                    const data = await response.json();
                    dispatch(setInitialTrades(data)); 
                }
            } catch (error) {
                console.error("Piyasa geçmişi çekilemedi:", error);
            }
        };

        fetchHistory();
    }, [dispatch, symbolPair]);

    return (
        <div className="bg-neutral-900/40 rounded-2xl border border-white/5 p-4 flex flex-col h-full backdrop-blur-sm">
            <h3 className="text-sm font-semibold text-neutral-400 uppercase tracking-wider mb-4">Piyasa Geçmişi</h3>
            <div className="flex-1 overflow-y-auto custom-scrollbar">
                {trades.length > 0 ? (
                    <div className="space-y-2">
                        {trades.map((trade, index) => (
                            <div key={index} className="flex justify-between text-xs py-1 border-b border-white/5 hover:bg-white/5 transition-colors px-1 rounded">
                                <span className={trade.side === 'BUY' ? 'text-emerald-500' : 'text-rose-500'}>
                                    {trade.price ? trade.price.toFixed(2) : '0.00'}
                                </span>
                                <span className="text-neutral-300">
                                    {trade.quantity ? trade.quantity.toFixed(4) : '0.0000'}
                                </span>
                                <span className="text-neutral-500">
                                    {trade.timestamp ? new Date(trade.timestamp).toLocaleTimeString() : '--:--:--'}
                                </span>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="text-center text-neutral-600 animate-pulse mt-10 text-sm">Canlı akış bekleniyor...</div>
                )}
            </div>
        </div>
    );
};

export default TradeHistory;
