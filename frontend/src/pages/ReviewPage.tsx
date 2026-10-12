import React, { useEffect, useState, useRef } from 'react';

interface Review {
  name: string;
  profession: string;
  text: string;
  imgSrc: string;
}

const defaultReviews: Review[] = [
  {
    name: 'Emily Rodriguez',
    profession: 'Tech Executive',
    text: '"The most incredible spa experience I\'ve ever had. Every treatment feels personalized and healing."',
    imgSrc: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?ixlib=rb-1.2.1&auto=format&fit=crop&w=250&q=80',
  },
  {
    name: 'Michael Thompson',
    profession: 'Wellness Consultant',
    text: '"I\'ve been coming to Serenity Spa for years. Their holistic approach to wellness is truly transformative."',
    imgSrc: 'https://images.unsplash.com/photo-1507003211169-0a1dd7bf1002?ixlib=rb-1.2.1&auto=format&fit=crop&w=250&q=80',
  },
  {
    name: 'Sarah Kim',
    profession: 'Marketing Director',
    text: '"The perfect escape from city stress. Their massage therapists are absolute miracle workers."',
    imgSrc: 'https://images.unsplash.com/photo-1534751516642-a1af1ef26a56?ixlib=rb-1.2.1&auto=format&fit=crop&w=250&q=80',
  },
];

const ReviewPage: React.FC = () => {
  const [reviews, setReviews] = useState<Review[]>(defaultReviews);
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [showThankYou, setShowThankYou] = useState(false);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    // Inject page-specific style
    const style = document.createElement('style');
    style.id = 'review-page-styles';
    style.textContent = `
      #review-form { padding-top: 150px; }
      @media screen and (max-width: 768px) { #review-form { padding-top: 120px; } }
      .testimonial { transition: transform 0.3s ease, box-shadow 0.3s ease; }
      .testimonial:hover { transform: translateY(-5px); box-shadow: 0 10px 20px rgba(0,0,0,0.1); }
    `;
    if (!document.getElementById('review-page-styles')) document.head.appendChild(style);
    return () => { document.getElementById('review-page-styles')?.remove(); };
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const form = formRef.current;
    if (!form) return;

    const fullName = (form.querySelector('#fullName') as HTMLInputElement).value;
    const profession = (form.querySelector('#profession') as HTMLInputElement).value;
    const reviewText = (form.querySelector('#reviewText') as HTMLTextAreaElement).value;

    if (!fullName || !reviewText || rating === 0) {
      alert('Please fill out all required fields and select a rating.');
      return;
    }

    const newReview: Review = {
      name: fullName,
      profession: profession,
      text: `"${reviewText}"`,
      imgSrc: 'https://images.unsplash.com/photo-1568602471122-7832951cc4c5?ixlib=rb-1.2.1&auto=format&fit=crop&w=250&q=80',
    };

    setReviews(prev => [newReview, ...prev]);
    setShowThankYou(true);
    setRating(0);
    setHoverRating(0);
    form.reset();
    setTimeout(() => setShowThankYou(false), 5000);
  };

  const activeRating = hoverRating || rating;

  return (
    <>
      {/* Review Form Section */}
      <section id="review-form" className="section" style={{ backgroundColor: '#f9f7f4', paddingBottom: '50px' }}>
        <div className="container">
          <h2 className="section-title">Share Your Experience</h2>
          <p className="section-subtitle">We value your feedback and would love to hear about your time with us</p>

          <form
            ref={formRef}
            id="reviewSubmitForm"
            onSubmit={handleSubmit}
            style={{ maxWidth: '700px', margin: '0 auto', backgroundColor: 'white', padding: '30px', borderRadius: '5px', boxShadow: '0 5px 15px rgba(0,0,0,0.05)' }}
          >
            <div style={{ marginBottom: '20px' }}>
              <label htmlFor="fullName" style={{ display: 'block', marginBottom: '5px', color: '#9b8578', fontWeight: 500, textAlign: 'left' }}>Full Name</label>
              <input type="text" id="fullName" name="fullName" required style={{ width: '100%', padding: '10px', border: '1px solid #e0e0e0', borderRadius: '3px', fontSize: '16px' }} />
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label htmlFor="profession" style={{ display: 'block', marginBottom: '5px', color: '#9b8578', fontWeight: 500, textAlign: 'left' }}>Profession</label>
              <input type="text" id="profession" name="profession" style={{ width: '100%', padding: '10px', border: '1px solid #e0e0e0', borderRadius: '3px', fontSize: '16px' }} />
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '5px', color: '#9b8578', fontWeight: 500, textAlign: 'left' }}>Rating</label>
              <div style={{ textAlign: 'left' }}>
                {[1, 2, 3, 4, 5].map(star => (
                  <span
                    key={star}
                    onClick={() => setRating(star)}
                    onMouseOver={() => setHoverRating(star)}
                    onMouseOut={() => setHoverRating(0)}
                    style={{
                      fontSize: '24px',
                      color: star <= activeRating ? '#ffc107' : '#e0e0e0',
                      cursor: 'pointer',
                      transition: 'color 0.3s ease',
                      marginRight: '5px',
                    }}
                  >★</span>
                ))}
                {rating > 0 && <span style={{ marginLeft: '10px', color: '#c9a287' }}>{rating}/5</span>}
              </div>
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label htmlFor="reviewText" style={{ display: 'block', marginBottom: '5px', color: '#9b8578', fontWeight: 500, textAlign: 'left' }}>Your Review</label>
              <textarea id="reviewText" name="reviewText" required rows={5} style={{ width: '100%', padding: '10px', border: '1px solid #e0e0e0', borderRadius: '3px', resize: 'vertical', fontSize: '16px' }}></textarea>
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label htmlFor="profileImage" style={{ display: 'block', marginBottom: '5px', color: '#9b8578', fontWeight: 500, textAlign: 'left' }}>Profile Image (Optional)</label>
              <input type="file" id="profileImage" name="profileImage" accept="image/*" style={{ width: '100%', padding: '10px', border: '1px solid #e0e0e0', borderRadius: '3px' }} />
            </div>

            <button type="submit" className="btn" style={{ backgroundColor: '#9b8578', color: 'white', border: 'none', width: '100%', cursor: 'pointer' }}>Submit Review</button>
          </form>

          {showThankYou && (
            <div style={{ marginTop: '20px', padding: '15px', backgroundColor: '#e8f5e8', color: '#3c763d', borderRadius: '3px', textAlign: 'center' }}>
              Thank you for your review! It has been submitted successfully.
            </div>
          )}
        </div>
      </section>

      {/* Testimonials Section */}
      <section id="testimonials" className="section testimonials">
        <div className="container">
          <h2 className="section-title">What Our Guests Say</h2>
          <p className="section-subtitle">Experiences that speak of transformation and renewal</p>

          <div className="testimonial-grid">
            {reviews.map((r, i) => (
              <div key={i} className="testimonial">
                <p>{r.text}</p>
                <div className="testimonial-author">
                  <img src={r.imgSrc} alt={r.name} />
                  <div className="author-info">
                    <h4>{r.name}</h4>
                    <span>{r.profession}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </>
  );
};

export default ReviewPage;
