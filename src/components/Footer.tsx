import { Facebook, Instagram, Scissors, Twitter } from "lucide-react"
import { Link } from "react-router-dom"

const LINKS = {
  Platforma: [
    { label: 'Pronađi Salon', href: '/salons' },
    { label: 'Za Salone', href: '/za-salone' },
    { label: 'Kako Funkcioniše', href: '/#how-it-works' },
    { label: 'Cijene', href: '/cijene' },
  ],
  Podrška: [
    { label: 'Pomoć', href: '/help' },
    { label: 'Kontakt', href: '/contact' },
    { label: 'FAQ', href: '/faq' },
  ],
  Pravno: [
    { label: 'Uvjeti Korištenja', href: '/terms' },
    { label: 'Privatnost', href: '/privacy' },
    { label: 'Kolačići', href: '/cookies' },
  ],
}

export default function Footer(){
    return (
    <footer className="bg-[#060912] border-t border-white/5 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-14">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-10">
          {/* Brand */}
          <div className="col-span-2 md:col-span-1">
            <Link to="/" className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-xl bg-gradient-to-br from-rose-500 to-rose-600 flex items-center justify-center">
                <Scissors className="w-4 h-4 text-white" strokeWidth={2.5} />
              </div>
              <span className="font-display font-bold text-lg">
                Br<span className="text-rose-500">i</span>co
              </span>
            </Link>
            <p className="text-slate-500 text-sm leading-relaxed max-w-[200px]">
              Rezerviši frizerski termin za manje od 60 sekundi.
            </p>
            <div className="flex items-center gap-3 mt-5">
              {[Instagram, Facebook, Twitter].map((Icon, i) => (
                <a
                  key={i}
                  href="#"
                  className="w-8 h-8 rounded-xl bg-white/5 hover:bg-white/10 flex items-center justify-center text-slate-400 hover:text-white transition-colors"
                >
                  <Icon className="w-4 h-4" />
                </a>
              ))}
            </div>
          </div>
 
          {/* Links */}
          {Object.entries(LINKS).map(([title, links]) => (
            <div key={title}>
              <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-widest mb-4">
                {title}
              </h4>
              <ul className="space-y-2.5">
                {links.map((link) => (
                  <li key={link.href}>
                    <Link
                      to={link.href}
                      className="text-sm text-slate-500 hover:text-white transition-colors"
                    >
                      {link.label}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>
 
        <div className="mt-12 pt-6 border-t border-white/5 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-600">
          <span>© {new Date().getFullYear()} Brico. Sva prava zadržana.</span>
          <span>Napravljeno za frizerske salone u BiH i regiji</span>
        </div>
      </div>
    </footer>
  )

}