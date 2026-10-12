import React, { useEffect, useState } from 'react';
import { contactApi } from '../services/serenityService';

const ContactPage: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [subject, setSubject] = useState('Website Enquiry');
  const [message, setMessage] = useState('');

  const [loading, setLoading] = useState(false);
  const [statusMsg, setStatusMsg] = useState<{ text: string; type: 'success' | 'error' } | null>(null);

  useEffect(() => {
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

    const style = document.createElement('style');
    style.id = 'contact-page-styles';
    style.textContent = `
      .contact-form input:focus, .contact-form textarea:focus {
        outline: none; border-color: #9b8578; box-shadow: 0 0 5px rgba(155,133,120,0.3);
      }
      .contact-form button { transition: all 0.3s ease; }
      .contact-form button:hover { background-color: #8a7467; }
    `;
    if (!document.getElementById('contact-page-styles')) document.head.appendChild(style);

    return () => {
      window.removeEventListener('scroll', handleScroll);
      document.getElementById('contact-page-styles')?.remove();
    };
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setStatusMsg(null);

    if (!name.trim() || !email.trim() || !message.trim()) {
      setStatusMsg({ text: 'Please fill in all required fields.', type: 'error' });
      return;
    }

    setLoading(true);

    try {
      await contactApi.submit({
        name,
        email,
        phone,
        subject: subject || 'General Enquiry',
        message,
      });

      setStatusMsg({ text: 'Thank you! Your message has been sent successfully.', type: 'success' });
      setName('');
      setEmail('');
      setPhone('');
      setMessage('');
    } catch (err: any) {
      const errorDetail = err.response?.data?.message || 'Failed to submit enquiry. Please try again.';
      setStatusMsg({ text: errorDetail, type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="section">
      <div className="container">
        <h1 className="section-title">Contact Serenity Spa</h1>
        <p className="section-subtitle">We're here to help you find your perfect wellness experience</p>

        <div className="contact-form">
          {statusMsg && (
            <div
              style={{
                padding: '15px',
                marginBottom: '20px',
                borderRadius: '4px',
                textAlign: 'center',
                backgroundColor: statusMsg.type === 'success' ? '#d4edda' : '#f8d7da',
                color: statusMsg.type === 'success' ? '#155724' : '#721c24',
                border: `1px solid ${statusMsg.type === 'success' ? '#c3e6cb' : '#f5c6cb'}`,
              }}
            >
              {statusMsg.text}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <input
              type="text"
              name="name"
              placeholder="Your Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
            <input
              type="email"
              name="email"
              placeholder="Your Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <input
              type="tel"
              name="phone"
              placeholder="Your Phone Number"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
            />
            <input
              type="text"
              name="subject"
              placeholder="Subject (e.g. Appointment Inquiry)"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
            />
            <textarea
              name="message"
              rows={5}
              placeholder="Your Message"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              required
            ></textarea>
            <button
              type="submit"
              disabled={loading}
              className="btn"
              style={{ cursor: loading ? 'not-allowed' : 'pointer' }}
            >
              {loading ? 'Sending...' : 'Send Message'}
            </button>
          </form>
        </div>

        <div className="section" style={{ textAlign: 'center', marginTop: '20px' }}>
          <h2 className="section-title">Contact Information</h2>
          <p>Phone: (555) 123-4567</p>
          <p>Email: info@serenityspa.com</p>
          <p>Address: 123 Tranquility Lane, Wellness Heights, CA 90210</p>
        </div>
      </div>
    </section>
  );
};

export default ContactPage;
