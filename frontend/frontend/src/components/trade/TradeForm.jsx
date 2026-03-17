import { useState } from 'react';

export const TradeForm = ({ symbolPair = 'BTC_USDT', userId = 3 }) => {
    
    const [side, setSide] = useState('BUY');
    const [price, setPrice] = useState('');
    const [quantity, setQuantity] = useState('');


    const total = (parseFloat(price) || 0) * (parseFloat(quantity) || 0);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!price || !quantity) return alert('Lütfen fiyat ve miktar girin!');


        const orderPayload = {
            userId: parseInt(userId),
            symbolPair,
            side,
            type: "LIMIT",
            price: parseFloat(price),
            quantity: parseFloat(quantity),
        };

        try {
            
            const response = await fetch('http://localhost:8080/api/v1/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(orderPayload),
            });

            if (response.ok) {
                console.log('Emir başarıyla gönderildi!');
                setQuantity(''); // Gönderdikten sonra sadece miktarı temizle (Fiyat kalıyor)
            } else {
                console.error('Emir gönderilemedi!');
            }
        } catch (error) {
            console.error('Bağlantı hatası:', error);
        }
    };

    return (
        <div className="bg-neutral-900/40 rounded-2xl border border-white/5 p-4 flex flex-col h-full backdrop-blur-sm font-sans">

            {/* Buy / Sell Fields */}
            <div className="flex bg-neutral-950 rounded-lg p-1 mb-4">
                <button
                    onClick={() => setSide('BUY')}
                    className={`flex-1 py-2 text-sm font-bold rounded-md transition-all ${side === 'BUY' ? 'bg-emerald-500/20 text-emerald-400' : 'text-neutral-500 hover:text-white'
                        }`}
                >
                    AL (BUY)
                </button>
                <button
                    onClick={() => setSide('SELL')}
                    className={`flex-1 py-2 text-sm font-bold rounded-md transition-all ${side === 'SELL' ? 'bg-rose-500/20 text-rose-400' : 'text-neutral-500 hover:text-white'
                        }`}
                >
                    SAT (SELL)
                </button>
            </div>

            <form onSubmit={handleSubmit} className="flex flex-col gap-3 flex-1">
                {/* Price Input */}
                <div className="flex bg-neutral-950 border border-white/10 rounded-lg px-3 py-2 focus-within:border-emerald-500/50 transition-colors">
                    <span className="text-neutral-500 text-sm self-center mr-2">Fiyat</span>
                    <input
                        type="number"
                        step="0.01"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        className="bg-transparent text-white text-right flex-1 outline-none font-mono"
                        placeholder="0.00"
                    />
                    <span className="text-neutral-500 text-sm self-center ml-2">USDT</span>
                </div>

                {/* Amount Input */}
                <div className="flex bg-neutral-950 border border-white/10 rounded-lg px-3 py-2 focus-within:border-emerald-500/50 transition-colors">
                    <span className="text-neutral-500 text-sm self-center mr-2">Miktar</span>
                    <input
                        type="number"
                        step="0.0001"
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                        className="bg-transparent text-white text-right flex-1 outline-none font-mono"
                        placeholder="0.00"
                    />
                    <span className="text-neutral-500 text-sm self-center ml-2">BTC</span>
                </div>

                {/* Total Amount Display */}
                <div className="mt-auto mb-3 flex justify-between px-1">
                    <span className="text-xs text-neutral-500">Toplam:</span>
                    <span className="text-sm text-white font-mono">{total.toFixed(2)} USDT</span>
                </div>

                {/* Send Button */}
                <button
                    type="submit"
                    className={`w-full py-3 rounded-lg font-bold text-white transition-all ${side === 'BUY' ? 'bg-emerald-600 hover:bg-emerald-500' : 'bg-rose-600 hover:bg-rose-500'
                        }`}
                >
                    {side === 'BUY' ? 'BTC AL' : 'BTC SAT'}
                </button>
            </form>
        </div>
    );
};

export default TradeForm;