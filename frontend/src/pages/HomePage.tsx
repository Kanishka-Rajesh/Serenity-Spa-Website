import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';

const HomePage: React.FC = () => {
  useEffect(() => {
    // ── 1. Header shrink on scroll ─────────────────────────────────────────
    const header = document.querySelector('header') as HTMLElement | null;
    const handleScroll = () => {
      if (!header) return;
      if (window.scrollY > 50) {
        header.style.padding = '10px 0';
        header.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.2)';
      } else {
        header.style.padding = '20px 0';
        header.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)';
      }
    };
    window.addEventListener('scroll', handleScroll);

    // ── 2. Smooth scrolling for anchor links ───────────────────────────────
    const anchorLinks = document.querySelectorAll<HTMLAnchorElement>('a[href^="#"]');
    const handleAnchorClick = function (this: HTMLAnchorElement, e: Event) {
      e.preventDefault();
      const targetId = this.getAttribute('href');
      if (!targetId || targetId === '#') return;
      const targetElement = document.querySelector(targetId) as HTMLElement | null;
      if (targetElement) {
        window.scrollTo({ top: targetElement.offsetTop - 80, behavior: 'smooth' });
      }
    };
    anchorLinks.forEach(link => link.addEventListener('click', handleAnchorClick));

    // ── 3. Mobile navigation toggle ────────────────────────────────────────
    const headerContent = document.querySelector('.header-content') as HTMLElement | null;
    let mobileMenuBtn: HTMLElement | null = null;
    if (headerContent && !document.querySelector('.mobile-menu-btn')) {
      mobileMenuBtn = document.createElement('div');
      mobileMenuBtn.className = 'mobile-menu-btn';
      mobileMenuBtn.innerHTML = '<span></span><span></span><span></span>';

      const mobileStyle = document.createElement('style');
      mobileStyle.id = 'mobile-menu-style';
      mobileStyle.textContent = `
        .mobile-menu-btn {
          display: none;
          flex-direction: column;
          justify-content: space-between;
          width: 30px;
          height: 21px;
          cursor: pointer;
        }
        .mobile-menu-btn span {
          display: block;
          height: 3px;
          width: 100%;
          background-color: #9b8578;
          transition: all 0.3s ease;
        }
        .mobile-menu-open {
          transform: translateX(0) !important;
          opacity: 1 !important;
        }
        @media screen and (max-width: 768px) {
          .mobile-menu-btn { display: flex; }
          nav ul {
            position: fixed;
            top: 60px;
            right: 0;
            flex-direction: column;
            background-color: rgba(255, 255, 255, 0.95);
            width: 80%;
            max-width: 300px;
            height: calc(100vh - 60px);
            padding: 30px;
            transform: translateX(100%);
            opacity: 0;
            transition: all 0.3s ease;
            display: flex !important;
            z-index: 1000;
            box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
          }
          nav ul li { margin: 15px 0; }
        }
      `;
      if (!document.getElementById('mobile-menu-style')) {
        document.head.appendChild(mobileStyle);
      }
      headerContent.appendChild(mobileMenuBtn);

      const toggleMenu = () => {
        const navMenu = document.querySelector('nav ul') as HTMLElement | null;
        if (!navMenu || !mobileMenuBtn) return;
        navMenu.classList.toggle('mobile-menu-open');
        const spans = mobileMenuBtn.querySelectorAll<HTMLElement>('span');
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
    }

    // ── 4. Testimonial carousel ────────────────────────────────────────────
    const setupTestimonialCarousel = () => {
      const testimonialSection = document.querySelector('.testimonials');
      if (!testimonialSection) return;
      const testimonials = document.querySelectorAll<HTMLElement>('.testimonial');
      if (testimonials.length <= 3) return;

      const testimonialGrid = document.querySelector('.testimonial-grid') as HTMLElement | null;
      if (!testimonialGrid) return;

      const controlsContainer = document.createElement('div');
      controlsContainer.className = 'carousel-controls';
      controlsContainer.innerHTML = `
        <button class="prev-btn">&#10094;</button>
        <div class="carousel-dots"></div>
        <button class="next-btn">&#10095;</button>
      `;

      const carouselStyle = document.createElement('style');
      carouselStyle.textContent = `
        .testimonial-grid { position: relative; overflow: hidden; }
        .carousel-slide { display: flex; transition: transform 0.5s ease; }
        .carousel-controls { display: flex; justify-content: center; align-items: center; margin-top: 30px; }
        .prev-btn, .next-btn { background: #9b8578; color: white; border: none; width: 40px; height: 40px; border-radius: 50%; cursor: pointer; font-size: 18px; margin: 0 10px; transition: background 0.3s ease; }
        .prev-btn:hover, .next-btn:hover { background: #c9a287; }
        .carousel-dots { display: flex; margin: 0 15px; }
        .dot { width: 12px; height: 12px; border-radius: 50%; background: #ddd; margin: 0 5px; cursor: pointer; transition: background 0.3s ease; }
        .dot.active { background: #9b8578; }
        @media screen and (max-width: 768px) { .carousel-slide .testimonial { flex: 0 0 100%; max-width: 100%; } }
      `;
      document.head.appendChild(carouselStyle);

      const carouselSlide = document.createElement('div');
      carouselSlide.className = 'carousel-slide';
      testimonialGrid.innerHTML = '';
      testimonialGrid.appendChild(carouselSlide);
      testimonials.forEach(t => carouselSlide.appendChild(t));

      const container = testimonialSection.querySelector('.container');
      if (container) container.appendChild(controlsContainer);

      const dotsContainer = document.querySelector('.carousel-dots');
      const numSlides = Math.ceil(testimonials.length / 3);
      let currentIndex = 0;

      const updateCarousel = () => {
        const slideWidth = testimonials[0].offsetWidth * 3;
        carouselSlide.style.transform = `translateX(-${currentIndex * slideWidth}px)`;
        document.querySelectorAll('.dot').forEach((dot, i) => {
          dot.classList.toggle('active', i === currentIndex);
        });
      };

      for (let i = 0; i < numSlides; i++) {
        const dot = document.createElement('div');
        dot.className = i === 0 ? 'dot active' : 'dot';
        dot.dataset.index = String(i);
        dotsContainer?.appendChild(dot);
        dot.addEventListener('click', () => { currentIndex = i; updateCarousel(); });
      }

      document.querySelector('.next-btn')?.addEventListener('click', () => {
        currentIndex = (currentIndex + 1) % numSlides;
        updateCarousel();
      });
      document.querySelector('.prev-btn')?.addEventListener('click', () => {
        currentIndex = (currentIndex - 1 + numSlides) % numSlides;
        updateCarousel();
      });
      window.addEventListener('resize', updateCarousel);
    };
    setupTestimonialCarousel();

    // ── 5. Gallery modal ───────────────────────────────────────────────────
    const setupGallery = () => {
      const galleryItems = document.querySelectorAll<HTMLElement>('.gallery-item');
      if (galleryItems.length === 0) return;

      const modal = document.createElement('div');
      modal.className = 'gallery-modal';
      modal.innerHTML = `
        <div class="modal-content">
          <span class="close-modal">&times;</span>
          <img src="" alt="Enlarged gallery image" />
        </div>
      `;

      const modalStyle = document.createElement('style');
      modalStyle.textContent = `
        .gallery-modal { display: none; position: fixed; z-index: 2000; left: 0; top: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.9); overflow: auto; }
        .modal-content { position: relative; margin: auto; padding: 20px; width: 80%; max-width: 1000px; height: 90%; display: flex; justify-content: center; align-items: center; }
        .modal-content img { max-width: 100%; max-height: 100%; object-fit: contain; }
        .close-modal { position: absolute; top: 20px; right: 30px; color: white; font-size: 40px; font-weight: bold; cursor: pointer; }
        .gallery-item { cursor: pointer; }
      `;
      document.head.appendChild(modalStyle);
      document.body.appendChild(modal);

      galleryItems.forEach(item => {
        item.addEventListener('click', () => {
          const img = item.querySelector('img') as HTMLImageElement | null;
          const modalImg = modal.querySelector('img') as HTMLImageElement | null;
          if (img && modalImg) { modalImg.src = img.src; }
          modal.style.display = 'block';
        });
      });

      const closeBtn = modal.querySelector('.close-modal') as HTMLElement | null;
      closeBtn?.addEventListener('click', () => { modal.style.display = 'none'; });
      const handleOutsideClick = (e: MouseEvent) => {
        if (e.target === modal) modal.style.display = 'none';
      };
      window.addEventListener('click', handleOutsideClick);

      return () => {
        window.removeEventListener('click', handleOutsideClick);
        if (document.body.contains(modal)) document.body.removeChild(modal);
      };
    };
    const galleryCleanup = setupGallery();

    // ── 6. Loading overlay ─────────────────────────────────────────────────
    if (!document.querySelector('.loading-overlay')) {
      const loadingOverlay = document.createElement('div');
      loadingOverlay.className = 'loading-overlay';
      loadingOverlay.innerHTML = '<div class="loading-spinner"></div>';

      const loadingStyle = document.createElement('style');
      loadingStyle.textContent = `
        .loading-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(255,255,255,0.9); display: flex; justify-content: center; align-items: center; z-index: 9999; transition: opacity 0.5s ease; }
        .loading-spinner { width: 50px; height: 50px; border: 5px solid #f3f3f3; border-top: 5px solid #9b8578; border-radius: 50%; animation: spin 1s linear infinite; }
        @keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); } }
      `;
      document.head.appendChild(loadingStyle);
      document.body.appendChild(loadingOverlay);

      // React's load event won't fire again — fade out immediately
      loadingOverlay.style.opacity = '0';
      setTimeout(() => {
        if (document.body.contains(loadingOverlay)) document.body.removeChild(loadingOverlay);
      }, 500);
    }

    // ── Cleanup ────────────────────────────────────────────────────────────
    return () => {
      window.removeEventListener('scroll', handleScroll);
      anchorLinks.forEach(link => link.removeEventListener('click', handleAnchorClick));
      if (mobileMenuBtn && headerContent && headerContent.contains(mobileMenuBtn)) {
        headerContent.removeChild(mobileMenuBtn);
      }
      if (galleryCleanup) galleryCleanup();
    };
  }, []);

  return (
    <>
      {/* ── Hero Section ─────────────────────────────────────── */}
      <section id="home" className="hero">
        <div className="hero-content">
          <h1>LUXURY WELLNESS RETREAT</h1>
          <p>Experience tranquility and rejuvenation in our award-winning spa sanctuary</p>
          <Link to="/services" className="btn">Explore Our Services</Link>
        </div>
      </section>

      {/* ── About Section ────────────────────────────────────── */}
      <section id="about" className="about">
        <div className="about-image"></div>
        <div className="about-content">
          <h2>Our Story</h2>
          <p>
            Founded in 2010, Serenity Spa was born from a passion to create a sanctuary where mind,
            body, and spirit could find perfect harmony. Our philosophy centers on holistic wellness,
            combining ancient healing traditions with modern luxury.
          </p>
          <p>
            Our team of expert therapists are masters in their craft, trained in techniques from around
            the world and dedicated to providing personalized experiences that address your unique needs.
          </p>
          <Link to="/contact" className="btn">Connect With Us</Link>
        </div>
      </section>
    </>
  );
};

export default HomePage;
