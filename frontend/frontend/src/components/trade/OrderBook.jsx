import { useDispatch, useSelector } from 'react-redux';
import { useEffect } from 'react';
import { setOrderBook } from '../../store/orderBookSlice';

export const OrderBook = ({ symbolPair = 'BTC_USDT' }) => {
    const { bids, asks } = useSelector((state) => state.orderBook || { bids: [], asks: [] });
    const dispatch = useDispatch();

    useEffect(() => {
        const fetchInitialOrderBook = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/v1/orders/${symbolPair}`);
                if (response.ok) {
                    const data = await response.json();
                    dispatch(setOrderBook(data));
                }
            } catch (error) {
                console.error("OrderBook ilk yükleme hatası:", error);
            }
        };
        fetchInitialOrderBook();
    }, [dispatch, symbolPair]);


    const aggregateOrders = (orders, isAsk) => {
        const grouped = {};
        orders.forEach(o => {
            const p = o.price.toFixed(2);
            grouped[p] = (grouped[p] || 0) + o.quantity;
        });

        let result = Object.entries(grouped).map(([price, quantity]) => ({
            price: parseFloat(price),
            quantity: quantity
        }));


        if (isAsk) {
            result.sort((a, b) => b.price - a.price);
        } else {
            // Alıcılar (Yeşil) için en pahalılar yukarıda kalsın
            result.sort((a, b) => b.price - a.price);
        }
        return result;
    };

    const groupedAsks = aggregateOrders(asks, true);
    const groupedBids = aggregateOrders(bids, false);

    return (
        <div className="bg-neutral-900/40 rounded-2xl border border-white/5 p-4 flex flex-col h-full backdrop-blur-sm font-mono">
            <h3 className="text-sm font-semibold text-neutral-400 uppercase tracking-wider mb-4 font-sans">Emir Defteri</h3>

            {/* Table Headers */}
            <div className="flex justify-between text-xs text-neutral-500 mb-2 px-1">
                <span>Fiyat</span>
                <span>Miktar</span>
                <span>Toplam</span>
            </div>

            {/* Satıcılar (Asks) - Kırmızı | overflow-y-auto eklendi */}
            <div className="flex-1 overflow-y-auto flex flex-col justify-end mb-2 space-y-1 custom-scrollbar pr-1">
                {groupedAsks.length > 0 ? groupedAsks.slice(-15).map((ask, i) => (
                    <div key={`ask-${i}`} className="flex justify-between text-xs hover:bg-white/5 px-1 py-1 rounded cursor-pointer transition-colors">
                        <span className="text-rose-500">{ask.price.toFixed(2)}</span>
                        <span className="text-neutral-300">{ask.quantity.toFixed(4)}</span>
                        <span className="text-neutral-500">{(ask.price * ask.quantity).toFixed(2)}</span>
                    </div>
                )) : <div className="text-center text-xs text-neutral-600">Satıcı Bekleniyor...</div>}
            </div>

            {/* Ortadaki Piyasa Fiyatı Ayracı */}
            <div className="py-2 border-y border-white/5 text-center text-lg font-bold text-white my-1 flex justify-center items-center gap-2">
                <span className="text-emerald-500">Spread</span>
            </div>

            {/* Alıcılar (Bids) - Yeşil | overflow-y-auto eklendi */}
            <div className="flex-1 overflow-y-auto flex flex-col space-y-1 mt-2 custom-scrollbar pr-1">
                {groupedBids.length > 0 ? groupedBids.slice(0, 15).map((bid, i) => (
                    <div key={`bid-${i}`} className="flex justify-between text-xs hover:bg-white/5 px-1 py-1 rounded cursor-pointer transition-colors">
                        <span className="text-emerald-500">{bid.price.toFixed(2)}</span>
                        <span className="text-neutral-300">{bid.quantity.toFixed(4)}</span>
                        <span className="text-neutral-500">{(bid.price * bid.quantity).toFixed(2)}</span>
                    </div>
                )) : <div className="text-center text-xs text-neutral-600 mt-2">Alıcı Bekleniyor...</div>}
            </div>
        </div>
    );
};

export default OrderBook;