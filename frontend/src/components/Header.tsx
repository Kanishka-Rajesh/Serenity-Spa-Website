import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Header: React.FC = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const headerContent = document.querySelector('.header-content');
    if (!headerContent) return;
    if (document.querySelector('.mobile-menu-btn')) return;

    const mobileMenuBtn = document.createElement('div');
    mobileMenuBtn.className = 'mobile-menu-btn';
    mobileMenuBtn.innerHTML = `
      <span></span>
      <span></span>
      <span></span>
    `;

    const style = document.createElement('style');
    style.textContent = `
      .mobile-menu-btn { display: none; flex-direction: column; justify-content: space-between; width: 30px; height: 21px; cursor: pointer; }
      .mobile-menu-btn span { display: block; height: 3px; width: 100%; background-color: #9b8578; transition: all 0.3s ease; }
      @media screen and (max-width: 768px) {
        .mobile-menu-btn { display: flex; }
        nav ul { position: fixed; top: 60px; right: 0; flex-direction: column; background-color: rgba(255,255,255,0.95); width: 80%; max-width: 300px; height: calc(100vh - 60px); padding: 30px; transform: translateX(100%); opacity: 0; transition: all 0.3s ease; display: flex !important; z-index: 1000; box-shadow: -2px 0 10px rgba(0,0,0,0.1); overflow-y: auto; }
        .mobile-menu-open { transform: translateX(0) !important; opacity: 1 !important; }
      }
    `;
    document.head.appendChild(style);
    headerContent.appendChild(mobileMenuBtn);

    const toggleMenu = () => {
      const navMenu = document.querySelector('nav ul');
      if (!navMenu) return;
      navMenu.classList.toggle('mobile-menu-open');
      const spans = mobileMenuBtn.querySelectorAll('span');
      if (navMenu.classList.contains('mobile-menu-open')) {
        spans[0].style.transform = 'rotate(45deg) translate(5px, 6px)';
        spans[1].style.opacity = '0';
        spans[2].style.transform = 'rotate(-45deg) translate(5px, -6px)';
      } else {
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
      }
    };
    mobileMenuBtn.addEventListener('click', toggleMenu);
  }, []);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header>
      <div className="container header-content">
        <Link to="/" className="logo">
          <span>Serenity</span> Spa
        </Link>
        <nav>
          <ul>
            <li><Link to="/">Home</Link></li>
            {isAuthenticated ? (
              <>
                {isAdmin ? (
                  <li><Link to="/admin">Admin</Link></li>
                ) : (
                  <>
                    <li><Link to="/services">Services</Link></li>
                    <li><Link to="/contact">Contact</Link></li>
                    <li><Link to="/membership">Membership</Link></li>
                    <li><Link to="/booking">Book Now</Link></li>
                    <li><Link to="/gallery">Gallery</Link></li>
                    <li><Link to="/review">Review</Link></li>
                    <li><Link to="/my-bookings">My Bookings</Link></li>
                  </>
                )}
                <li>
                  <button
                    onClick={handleLogout}
                    style={{
                      background: 'none',
                      border: 'none',
                      color: '#9b8578',
                      fontFamily: "'Cormorant Garamond', serif",
                      fontSize: '16px',
                      fontWeight: 500,
                      letterSpacing: '1px',
                      cursor: 'pointer',
                      padding: 0,
                    }}
                  >
                    Logout ({user?.fullName?.split(' ')[0]})
                  </button>
                </li>
              </>
            ) : (
              <li><Link to="/login">Login</Link></li>
            )}
          </ul>
        </nav>
      </div>
    </header>
  );
};

export default Header;
