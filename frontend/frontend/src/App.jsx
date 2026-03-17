import Navbar from './components/layout/Navbar';
import TradeHistory from './components/trade/TradeHistory';
import OrderBook from './components/trade/OrderBook';
import TradeForm from './components/trade/TradeForm'; 

function App() {
  return (
    <div className="h-screen bg-neutral-950 flex flex-col font-sans overflow-hidden">
      <Navbar />

      <main className="flex-1 p-3 grid grid-cols-1 md:grid-cols-12 gap-3 max-w-[1600px] mx-auto w-full min-h-0">
        
        <aside className="md:col-span-3 flex flex-col min-h-0">
          <OrderBook />
        </aside>

        <section className="md:col-span-6 flex flex-col gap-3 min-h-0">
          
          <div className="flex-1 bg-neutral-900/10 rounded-2xl border border-white/5 flex items-center justify-center">
             <span className="text-neutral-700 text-sm tracking-widest uppercase">
               Hoş Geldiniz
             </span>
          </div>
          
          <div className="shrink-0">
             <TradeForm />
          </div>

        </section>

        <aside className="md:col-span-3 flex flex-col min-h-0">
          <TradeHistory />
        </aside>

      </main>
    </div>
  );
}

export default App;