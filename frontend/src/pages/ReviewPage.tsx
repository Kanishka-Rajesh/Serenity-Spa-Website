import React, { useEffect, useState, useRef } from 'react';
import api from '../services/api';

interface PublicReview {
  id: number;
  fullName: string;
  profession: string;
  rating: number;
  comment: string;
  createdAt: string;
}

const defaultTestimonials = [
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
  const [reviews, setReviews] = useState<PublicReview[]>([]);
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [showThankYou, setShowThankYou] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
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

  // Load existing reviews from backend on mount
  useEffect(() => {
    api.get<PublicReview[]>('/public-reviews')
      .then(res => setReviews(res.data))
      .catch(() => setReviews([]));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const form = formRef.current;
    if (!form) return;

    const fullName = (form.querySelector('#fullName') as HTMLInputElement).value.trim();
    const profession = (form.querySelector('#profession') as HTMLInputElement).value.trim();
    const comment = (form.querySelector('#reviewText') as HTMLTextAreaElement).value.trim();

    if (!fullName || !comment || rating === 0) {
      alert('Please fill out all required fields and select a rating.');
      return;
    }

    setSubmitting(true);
    setError(null);
    try {
      const res = await api.post<PublicReview>('/public-reviews', {
        fullName,
        profession,
        rating,
        comment,
      });
      // Prepend the new review to the list
      setReviews(prev => [res.data, ...prev]);
      setShowThankYou(true);
      setRating(0);
      setHoverRating(0);
      form.reset();
      setTimeout(() => setShowThankYou(false), 5000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit review. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const activeRating = hoverRating || rating;

  // Merge backend reviews + default testimonials for display
  const testimonialList = reviews.length > 0 ? reviews : null;

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

            <button
              type="submit"
              className="btn"
              disabled={submitting}
              style={{ backgroundColor: '#9b8578', color: 'white', border: 'none', width: '100%', cursor: submitting ? 'not-allowed' : 'pointer', opacity: submitting ? 0.7 : 1 }}
            >
              {submitting ? 'Submitting…' : 'Submit Review'}
            </button>
          </form>

          {showThankYou && (
            <div style={{ marginTop: '20px', padding: '15px', backgroundColor: '#e8f5e8', color: '#3c763d', borderRadius: '3px', textAlign: 'center' }}>
              ✓ Thank you for your review! It has been submitted successfully.
            </div>
          )}
          {error && (
            <div style={{ marginTop: '20px', padding: '15px', backgroundColor: '#fff5f5', color: '#c53030', borderRadius: '3px', textAlign: 'center' }}>
              {error}
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
            {testimonialList
              ? testimonialList.map((r) => (
                <div key={r.id} className="testimonial">
                  <p>"{r.comment}"</p>
                  <div className="testimonial-author">
                    <img
                      src="https://images.unsplash.com/photo-1568602471122-7832951cc4c5?ixlib=rb-1.2.1&auto=format&fit=crop&w=250&q=80"
                      alt={r.fullName}
                    />
                    <div className="author-info">
                      <h4>{r.fullName}</h4>
                      <span>{r.profession || 'Guest'}</span>
                      <span style={{ color: '#ffc107', marginLeft: '8px' }}>{'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)}</span>
                    </div>
                  </div>
                </div>
              ))
              : defaultTestimonials.map((r, i) => (
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
              ))
            }
          </div>
        </div>
      </section>
    </>
  );
};

export default ReviewPage;
