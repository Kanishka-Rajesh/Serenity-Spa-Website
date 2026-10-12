import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { treatmentsApi, appointmentsApi } from '../services/serenityService';
import type { Treatment, PriceCalculation } from '../types';
import { useAuth } from '../context/AuthContext';

const BookingPage: React.FC = () => {
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const [treatments, setTreatments] = useState<Treatment[]>([]);
  const [selectedTreatmentId, setSelectedTreatmentId] = useState<number | ''>('');
  const [date, setDate] = useState('');
  const [time, setTime] = useState('');
  const [notes, setNotes] = useState('');

  // Server-calculated price breakdown
  const [priceCalculation, setPriceCalculation] = useState<PriceCalculation | null>(null);
  const [calculatingPrice, setCalculatingPrice] = useState(false);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    treatmentsApi.getAll()
      .then((res) => {
        setTreatments(res.data);
        const storedServiceName = localStorage.getItem('serviceName');
        const storedServiceId = localStorage.getItem('selectedService');
        if (storedServiceName && res.data.length > 0) {
          const match = res.data.find(t => t.name.toLowerCase().includes(storedServiceName.toLowerCase()));
          if (match) setSelectedTreatmentId(match.id);
        } else if (storedServiceId && res.data.length > 0) {
          const match = res.data.find(t => t.name.toLowerCase().includes(storedServiceId.toLowerCase()));
          if (match) setSelectedTreatmentId(match.id);
        }
      })
      .catch((err) => {
        console.error('Failed to load treatments', err);
      });
  }, []);

  // Fetch backend authoritative price calculation whenever selected treatment changes
  useEffect(() => {
    if (selectedTreatmentId) {
      setCalculatingPrice(true);
      appointmentsApi.calculatePrice(Number(selectedTreatmentId))
        .then((res) => setPriceCalculation(res.data))
        .catch((err) => console.error('Price calculation error', err))
        .finally(() => setCalculatingPrice(false));
    } else {
      setPriceCalculation(null);
    }
  }, [selectedTreatmentId, isAuthenticated]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!isAuthenticated) {
      setError('Please log in or register before scheduling an appointment.');
      return;
    }

    if (!selectedTreatmentId) {
      setError('Please select a treatment.');
      return;
    }

    setLoading(true);

    try {
      await appointmentsApi.book({
        treatmentId: Number(selectedTreatmentId),
        appointmentDate: date,
        startTime: time,
        notes: notes,
      });

      setSuccess('Your appointment has been confirmed successfully!');
      setDate('');
      setTime('');
      setNotes('');
      setTimeout(() => {
        navigate('/my-bookings');
      }, 1800);
    } catch (err: any) {
      const msg = err.response?.data?.message || err.response?.data?.error || 'Failed to book appointment. The chosen slot may already be reserved.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="section">
      <div className="container">
        <h1 className="section-title">Book Your Wellness Experience</h1>
        <p className="section-subtitle">Personalize your spa journey with our expert therapists</p>

        <div className="booking-form" style={{ maxWidth: '650px' }}>
          {error && (
            <div style={{ padding: '12px', marginBottom: '15px', backgroundColor: '#fde8e8', color: '#c53030', borderRadius: '4px' }}>
              {error}
              {!isAuthenticated && (
                <div style={{ marginTop: '8px' }}>
                  <Link to="/login" style={{ color: '#9b8578', fontWeight: 600, textDecoration: 'underline' }}>
                    Click here to Sign In or Register
                  </Link>
                </div>
              )}
            </div>
          )}

          {success && (
            <div style={{ padding: '12px', marginBottom: '15px', backgroundColor: '#e8f5e8', color: '#3c763d', borderRadius: '4px' }}>
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <input
              type="text"
              name="name"
              placeholder="Your Name"
              defaultValue={user?.fullName || ''}
              required
            />
            <input
              type="email"
              name="email"
              placeholder="Your Email"
              defaultValue={user?.email || ''}
              required
            />
            <input
              type="tel"
              name="phone"
              placeholder="Your Phone Number"
            />

            <select
              name="service"
              value={selectedTreatmentId}
              onChange={(e) => setSelectedTreatmentId(e.target.value ? Number(e.target.value) : '')}
              required
            >
              <option value="">Select Treatment</option>
              {treatments.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.name} - ${t.price} ({t.durationMinutes} mins)
                </option>
              ))}
            </select>

            <input
              type="date"
              name="date"
              min={new Date().toISOString().split('T')[0]}
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
            />
            <input
              type="time"
              name="time"
              value={time}
              onChange={(e) => setTime(e.target.value)}
              required
            />

            <textarea
              name="additional-notes"
              rows={3}
              placeholder="Additional Notes or Special Requests"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
            ></textarea>

            {/* Authoritative Server-Calculated Price Breakdown */}
            {priceCalculation && (
              <div
                style={{
                  backgroundColor: '#fbf9f6',
                  border: '1px solid #eadfd6',
                  borderRadius: '5px',
                  padding: '16px 20px',
                  marginBottom: '20px',
                  fontSize: '15px',
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', color: '#666' }}>
                  <span>Treatment price:</span>
                  <span style={{ fontWeight: 500 }}>${priceCalculation.originalPrice.toFixed(2)}</span>
                </div>

                {priceCalculation.membershipActive ? (
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', color: '#2e7d32' }}>
                    <span>
                      Membership discount ({priceCalculation.membershipPlanName || 'Active'} - {priceCalculation.discountPercentage}%):
                    </span>
                    <span style={{ fontWeight: 600 }}>-${priceCalculation.discountAmount.toFixed(2)}</span>
                  </div>
                ) : (
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', color: '#888', fontSize: '13px' }}>
                    <span>Membership discount:</span>
                    <span>
                      None (
                      <Link to="/membership" style={{ color: '#c9a287', textDecoration: 'underline' }}>
                        Join Membership to save up to 20%
                      </Link>
                      )
                    </span>
                  </div>
                )}

                <div
                  style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    borderTop: '1px solid #e2d3c7',
                    paddingTop: '8px',
                    marginTop: '8px',
                    color: '#9b8578',
                    fontSize: '17px',
                    fontWeight: 600,
                  }}
                >
                  <span>Final price:</span>
                  <span style={{ color: '#2b2b2b' }}>${priceCalculation.finalPrice.toFixed(2)}</span>
                </div>
              </div>
            )}

            <button
              type="submit"
              disabled={loading || calculatingPrice}
              className="btn"
              style={{
                width: '100%',
                backgroundColor: '#9b8578',
                color: 'white',
                border: 'none',
                cursor: loading ? 'not-allowed' : 'pointer',
              }}
            >
              {loading ? 'Confirming Reservation...' : 'Confirm Booking'}
            </button>
          </form>
        </div>
      </div>
    </section>
  );
};

export default BookingPage;
