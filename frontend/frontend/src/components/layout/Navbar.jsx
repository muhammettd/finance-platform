
const Navbar = () => {
    return (
        <nav className="h-14 border-b border-white/5 bg-neutral-900/50 backdrop-blur-md flex items-center px-6 sticky top-0 z-50">
            <div className="flex items-center gap-2">
                <div className="w-8 h-8 bg-emerald-500 rounded-lg flex items-center justify-center font-bold text-black text-xl italic shadow-[0_0_15px_rgba(16,185,129,0.4)]">F</div>
                <span className="text-lg font-bold tracking-tight text-white">
                    FINANCE<span className="text-emerald-500 font-extrabold italic">PRO</span>
                </span>
            </div>
        </nav>
    );
}

export default Navbar