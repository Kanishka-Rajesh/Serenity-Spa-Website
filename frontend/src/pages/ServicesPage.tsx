import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

const ServicesPage: React.FC = () => {
  const [activeFilter, setActiveFilter] = useState('all');

  useEffect(() => {
    // Header shrink on scroll
    const header = document.querySelector('header') as HTMLElement | null;
    const handleScroll = () => {
      if (!header) return;
      if (window.scrollY > 50) {
        header.style.padding = '10px 0';
        header.style.boxShadow = '0 2px 10px rgba(0,0,0,0.2)';
      } else {
        header.style.padding = '20px 0';
        header.style.boxShadow = '0 2px 10px rgba(0,0,0,0.1)';
      }
    };
    window.addEventListener('scroll', handleScroll);

    // Service card hover effects (matching original)
    const cards = document.querySelectorAll<HTMLElement>('.service-preview');
    const onEnter = function (this: HTMLElement) {
      this.style.transform = 'translateY(-10px)';
      this.style.boxShadow = '0 15px 30px rgba(0,0,0,0.1)';
    };
    const onLeave = function (this: HTMLElement) {
      this.style.transform = 'translateY(0)';
      this.style.boxShadow = '0 5px 15px rgba(0,0,0,0.05)';
    };
    cards.forEach(c => {
      c.addEventListener('mouseenter', onEnter);
      c.addEventListener('mouseleave', onLeave);
    });

    // Store selected service in localStorage when Book btn clicked
    const bookBtns = document.querySelectorAll<HTMLElement>('.service-preview .btn');
    const onBookClick = function (this: HTMLElement) {
      const card = this.closest('.service-preview') as HTMLElement | null;
      if (card?.id) {
        localStorage.setItem('selectedService', card.id);
        const h3 = card.querySelector('h3');
        if (h3) localStorage.setItem('serviceName', h3.textContent || '');
      }
    };
    bookBtns.forEach(b => b.addEventListener('click', onBookClick));

    return () => {
      window.removeEventListener('scroll', handleScroll);
      cards.forEach(c => {
        c.removeEventListener('mouseenter', onEnter);
        c.removeEventListener('mouseleave', onLeave);
      });
      bookBtns.forEach(b => b.removeEventListener('click', onBookClick));
    };
  }, []);

  const services = [
    { id: 'massage', label: 'Massage' },
    { id: 'facial', label: 'Facial' },
    { id: 'body', label: 'Body Treatments' },
  ];

  const isVisible = (id: string) => activeFilter === 'all' || activeFilter === id;

  return (
    <section className="section">
      <div className="container">
        <h1 className="section-title">Our Signature Services</h1>
        <p className="section-subtitle">Holistic wellness experiences tailored to you</p>

        {/* Filter buttons (injected by original JS) */}
        <div className="service-filters">
          <button
            className={`filter-btn${activeFilter === 'all' ? ' active' : ''}`}
            onClick={() => setActiveFilter('all')}
          >All Services</button>
          {services.map(s => (
            <button
              key={s.id}
              className={`filter-btn${activeFilter === s.id ? ' active' : ''}`}
              onClick={() => setActiveFilter(s.id)}
            >{s.label}</button>
          ))}
        </div>

        <div className="featured-services">
          <div id="massage" className="service-preview" style={{ display: isVisible('massage') ? '' : 'none' }}>
            <img
              src="https://images.unsplash.com/photo-1519415510236-718bdfcd89c8?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80"
              alt="Massage Therapy"
            />
            <h3>Luxury Massage Therapy</h3>
            <p>Choose from a variety of massage techniques including:</p>
            <ul>
              <li>Swedish Relaxation Massage</li>
              <li>Deep Tissue Therapeutic Massage</li>
              <li>Hot Stone Massage</li>
              <li>Aromatherapy Massage</li>
              <li>Couples Massage</li>
            </ul>
            <Link to="/booking" className="btn">Book Massage</Link>
          </div>

          <div id="facial" className="service-preview" style={{ display: isVisible('facial') ? '' : 'none' }}>
            <img
              src="https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80"
              alt="Facial Treatments"
            />
            <h3>Advanced Facial Treatments</h3>
            <p>Rejuvenate and restore your skin with our premium treatments:</p>
            <ul>
              <li>Hydrating Oxygen Facial</li>
              <li>Anti-Aging Collagen Treatment</li>
              <li>Detoxifying Charcoal Facial</li>
              <li>Vitamin C Brightening Facial</li>
              <li>Sensitive Skin Calming Facial</li>
            </ul>
            <Link to="/booking" className="btn">Book Facial</Link>
          </div>

          <div id="body" className="service-preview" style={{ display: isVisible('body') ? '' : 'none' }}>
            <img
              src="https://images.squarespace-cdn.com/content/v1/582c39e5579fb3800810246e/1486675869104-AG2VY4QN7FMD3AW2FJ20/image-asset.jpeg?format=1500w"
              alt="Body Rituals"
            />
            <h3>Holistic Body Rituals</h3>
            <p>Comprehensive body treatments for complete wellness:</p>
            <ul>
              <li>Full Body Exfoliation</li>
              <li>Detox Body Wrap</li>
              <li>Hydrating Body Treatment</li>
              <li>Aromatherapy Body Scrub</li>
              <li>Ayurvedic Body Renewal</li>
            </ul>
            <Link to="/booking" className="btn">Book Body Treatment</Link>
          </div>
        </div>
      </div>
    </section>
  );
};

export default ServicesPage;
