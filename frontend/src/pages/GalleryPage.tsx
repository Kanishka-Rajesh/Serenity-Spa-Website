import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';

const galleryImages = [
  { src: 'https://images.unsplash.com/photo-1600334129128-685c5582fd35?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', alt: 'Spa Interior' },
  { src: 'https://images.unsplash.com/photo-1580618672591-eb180b1a973f?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', alt: 'Treatment Room' },
  { src: 'https://images.unsplash.com/photo-1515377905703-c4788e51af15?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', alt: 'Relaxation Area' },
  { src: 'https://images.unsplash.com/photo-1445019980597-93fa8acb246c?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', alt: 'Pool' },
  { src: 'https://images.pexels.com/photos/3865676/pexels-photo-3865676.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2', alt: 'Products' },
  { src: 'https://images.unsplash.com/photo-1552693673-1bf958298935?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', alt: 'Spa Treatment' },
  { src: 'https://images.unsplash.com/photo-1544161515-4ab6ce6db874?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', alt: 'Massage' },
  { src: 'https://images.unsplash.com/photo-1540555700478-4be289fbecef?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80', alt: 'Outdoor Area' },
  { src: 'https://images.pexels.com/photos/6620948/pexels-photo-6620948.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2', alt: 'Spa Lounge' },
  { src: 'https://images.pexels.com/photos/10832255/pexels-photo-10832255.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2', alt: 'Serenity Spa' },
  { src: 'https://images.pexels.com/photos/5659019/pexels-photo-5659019.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2', alt: 'Spa Lounge' },
  { src: 'https://i.pinimg.com/originals/ab/75/36/ab7536a2494956799e1127c0df3d1527.jpg', alt: 'Spa Lounge' },
];

const GalleryPage: React.FC = () => {
  useEffect(() => {
    // Lightbox modal (port of original initializeGalleryLightbox)
    const modal = document.createElement('div');
    modal.className = 'gallery-modal';
    modal.innerHTML = `
      <div class="modal-content">
        <span class="close-button">&times;</span>
        <img class="modal-image" src="" alt="Full size image" />
        <div class="modal-navigation">
          <button class="nav-button prev-button">&lt;</button>
          <button class="nav-button next-button">&gt;</button>
        </div>
        <div class="image-caption"></div>
      </div>
    `;
    document.body.appendChild(modal);

    const lightboxStyle = document.createElement('style');
    lightboxStyle.id = 'gallery-lightbox-style';
    lightboxStyle.textContent = `
      .gallery-modal { display:none; position:fixed; z-index:2000; left:0; top:0; width:100%; height:100%; background-color:rgba(0,0,0,0.9); overflow:hidden; }
      .gallery-modal .modal-content { position:relative; margin:auto; padding:0; width:90%; max-width:1200px; height:90%; display:flex; flex-direction:column; justify-content:center; align-items:center; }
      .modal-image { max-width:100%; max-height:80vh; object-fit:contain; }
      .close-button { position:absolute; top:15px; right:15px; color:white; font-size:40px; font-weight:bold; cursor:pointer; z-index:2001; }
      .modal-navigation { width:100%; display:flex; justify-content:space-between; position:absolute; top:50%; transform:translateY(-50%); }
      .nav-button { background:rgba(255,255,255,0.2); border:none; color:white; font-size:24px; padding:15px; cursor:pointer; border-radius:50%; width:50px; height:50px; display:flex; align-items:center; justify-content:center; transition:background-color 0.3s; }
      .nav-button:hover { background:rgba(255,255,255,0.4); }
      .image-caption { color:white; padding:10px; text-align:center; margin-top:15px; font-size:18px; }
      .fade-in { animation: fadeIn 0.5s; }
      @keyframes fadeIn { from{opacity:0} to{opacity:1} }
      body.modal-open { overflow:hidden; }
      .extended-gallery-item { cursor:pointer; position:relative; }
      .gallery-overlay { position:absolute; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.4); display:flex; justify-content:center; align-items:center; opacity:0; transition:opacity 0.3s ease; }
      .extended-gallery-item:hover .gallery-overlay { opacity:1; }
      .overlay-content { color:white; text-align:center; }
      .overlay-content h3 { font-size:1.3rem; margin-bottom:5px; }
      .overlay-content p { font-size:0.9rem; }
    `;
    if (!document.getElementById('gallery-lightbox-style')) document.head.appendChild(lightboxStyle);

    const galleryItems = document.querySelectorAll<HTMLElement>('.extended-gallery-item');
    const modalImg = modal.querySelector('.modal-image') as HTMLImageElement;
    const caption = modal.querySelector('.image-caption') as HTMLElement;
    const closeBtn = modal.querySelector('.close-button') as HTMLElement;
    const prevBtn = modal.querySelector('.prev-button') as HTMLElement;
    const nextBtn = modal.querySelector('.next-button') as HTMLElement;
    let currentIndex = 0;

    function openModal(src: string, alt: string) {
      modal.style.display = 'block';
      modalImg.src = src;
      modalImg.classList.add('fade-in');
      caption.textContent = alt || 'Serenity Spa';
      document.body.classList.add('modal-open');
    }
    function closeModal() {
      modal.style.display = 'none';
      document.body.classList.remove('modal-open');
    }

    galleryItems.forEach((item, idx) => {
      // Add overlay
      const overlay = document.createElement('div');
      overlay.className = 'gallery-overlay';
      const img = item.querySelector('img') as HTMLImageElement;
      const cap = img?.alt || 'Serenity Spa';
      overlay.innerHTML = `<div class="overlay-content"><h3>${cap}</h3><p>Click to view</p></div>`;
      item.appendChild(overlay);

      item.addEventListener('click', () => {
        currentIndex = idx;
        if (img) openModal(img.src, img.alt);
      });
    });

    closeBtn?.addEventListener('click', closeModal);
    modal.addEventListener('click', (e) => { if (e.target === modal) closeModal(); });
    prevBtn?.addEventListener('click', () => {
      currentIndex = (currentIndex - 1 + galleryItems.length) % galleryItems.length;
      const img = galleryItems[currentIndex].querySelector('img') as HTMLImageElement;
      if (img) { modalImg.src = img.src; modalImg.classList.add('fade-in'); caption.textContent = img.alt || 'Serenity Spa'; }
    });
    nextBtn?.addEventListener('click', () => {
      currentIndex = (currentIndex + 1) % galleryItems.length;
      const img = galleryItems[currentIndex].querySelector('img') as HTMLImageElement;
      if (img) { modalImg.src = img.src; modalImg.classList.add('fade-in'); caption.textContent = img.alt || 'Serenity Spa'; }
    });
    const keyHandler = (e: KeyboardEvent) => {
      if (modal.style.display === 'block') {
        if (e.key === 'Escape') closeModal();
        else if (e.key === 'ArrowLeft') prevBtn?.click();
        else if (e.key === 'ArrowRight') nextBtn?.click();
      }
    };
    document.addEventListener('keydown', keyHandler);
    modalImg.addEventListener('animationend', () => modalImg.classList.remove('fade-in'));

    return () => {
      document.removeEventListener('keydown', keyHandler);
      if (document.body.contains(modal)) document.body.removeChild(modal);
      document.getElementById('gallery-lightbox-style')?.remove();
      document.body.classList.remove('modal-open');
    };
  }, []);

  return (
    <>
      {/* Gallery Hero */}
      <section className="gallery-hero">
        <div className="gallery-hero-content">
          <h1>Our Sanctuary</h1>
          <p>A visual journey through our tranquil spaces and transformative experiences</p>
        </div>
      </section>

      {/* Gallery Grid */}
      <section className="extended-gallery">
        {galleryImages.map((img, i) => (
          <div key={i} className="extended-gallery-item">
            <img src={img.src} alt={img.alt} />
          </div>
        ))}
      </section>

      {/* CTA */}
      <section className="cta">
        <h2>Experience Our Sanctuary</h2>
        <p>Every corner of Serenity Spa is designed to transport you to a world of complete relaxation and rejuvenation.</p>
        <Link to="/booking" className="btn">Book Your Experience</Link>
      </section>
    </>
  );
};

export default GalleryPage;
