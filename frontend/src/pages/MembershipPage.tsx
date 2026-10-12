import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { membershipsApi } from '../services/serenityService';
import type { MembershipPlan, UserMembership } from '../types';
import { useAuth } from '../context/AuthContext';

const MembershipPage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const [plans, setPlans] = useState<MembershipPlan[]>([]);
  const [myMembership, setMyMembership] = useState<UserMembership | null>(null);
  const [loading, setLoading] = useState(true);
  const [activatingPlanId, setActivatingPlanId] = useState<number | null>(null);
  const [message, setMessage] = useState<{ text: string; type: 'success' | 'error' } | null>(null);

  const fetchMembershipData = () => {
    setLoading(true);
    membershipsApi.getPlans()
      .then((res) => setPlans(res.data))
      .catch((err) => console.error('Failed to load membership plans', err));

    if (isAuthenticated) {
      membershipsApi.getMyActive()
        .then((res) => {
          if (res.status === 200) {
            setMyMembership(res.data);
          } else {
            setMyMembership(null);
          }
        })
        .catch(() => setMyMembership(null))
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMembershipData();
  }, [isAuthenticated]);

  const handleActivate = async (planId: number, planName: string) => {
    if (!isAuthenticated) {
      alert('Please log in first to purchase or activate a membership.');
      return;
    }

    if (!window.confirm(`Activate ${planName}? This will enable your member discounts immediately (Demo verified flow).`)) {
      return;
    }

    setActivatingPlanId(planId);
    setMessage(null);

    try {
      const res = await membershipsApi.activate(planId, true);
      setMyMembership(res.data);
      setMessage({
        text: `Success! Your ${res.data.planName} membership is now active until ${res.data.endDate}. Member discounts will apply automatically at booking!`,
        type: 'success',
      });
    } catch (err: any) {
      setMessage({
        text: err.response?.data?.message || 'Failed to activate membership. Please try again.',
        type: 'error',
      });
    } finally {
      setActivatingPlanId(null);
    }
  };

  return (
    <>
      {/* Membership Hero */}
      <section className="membership-hero">
        <div className="hero-content">
          <h1>Exclusive Membership</h1>
          <p>Unlock endless wellness and transformative experiences</p>
        </div>
      </section>

      {/* Customer Current Membership Status Bar */}
      {isAuthenticated && myMembership && (
        <section style={{ backgroundColor: '#f3ece6', padding: '25px 20px', borderBottom: '1px solid #e2d7ce' }}>
          <div className="container" style={{ maxWidth: '900px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '15px' }}>
            <div>
              <span style={{ fontSize: '13px', textTransform: 'uppercase', letterSpacing: '2px', color: '#9b8578', fontWeight: 600 }}>Active Member Status</span>
              <h3 style={{ color: '#554238', margin: '4px 0 6px 0', fontSize: '1.6rem' }}>{myMembership.planName}</h3>
              <p style={{ margin: 0, color: '#6d5a50', fontSize: '0.95rem' }}>
                Enjoying <strong>{myMembership.discountPercentage}% discount</strong> on treatments &nbsp;|&nbsp; Valid through: <strong>{myMembership.endDate}</strong>
              </p>
            </div>
            <div>
              <Link to="/booking" className="btn" style={{ fontSize: '14px', padding: '10px 20px' }}>Book with Member Discount</Link>
            </div>
          </div>
        </section>
      )}

      {/* Feedback Message */}
      {message && (
        <div className="container" style={{ maxWidth: '800px', marginTop: '25px' }}>
          <div
            style={{
              padding: '15px 20px',
              borderRadius: '4px',
              textAlign: 'center',
              backgroundColor: message.type === 'success' ? '#e8f5e8' : '#fde8e8',
              color: message.type === 'success' ? '#2e7d32' : '#c53030',
              border: `1px solid ${message.type === 'success' ? '#c8e6c9' : '#f8b4b4'}`,
            }}
          >
            {message.text}
          </div>
        </div>
      )}

      {/* Plans Section */}
      <section className="membership-plans container">
        {loading ? (
          <p style={{ textAlign: 'center', color: '#9b8578', width: '100%', fontSize: '1.2rem' }}>Loading membership plans...</p>
        ) : plans.length === 0 ? (
          <p style={{ textAlign: 'center', color: '#777', width: '100%' }}>No membership plans currently available.</p>
        ) : (
          plans.map((p) => {
            const isCurrentPlan = myMembership?.planId === p.id && myMembership.activeNow;
            const benefitsList = p.benefits ? p.benefits.split(',').map((b) => b.trim()) : [];

            return (
              <div key={p.id} className="membership-plan">
                <h3>{p.name}</h3>
                <div className="price">${p.price} / month</div>
                <div style={{ color: '#c9a287', fontWeight: 600, fontSize: '15px', marginBottom: '15px' }}>
                  {p.discountPercentage}% Discount on Spa Treatments
                </div>
                <ul>
                  {benefitsList.map((benefit, idx) => (
                    <li key={idx}>{benefit}</li>
                  ))}
                </ul>

                {isCurrentPlan ? (
                  <button
                    onClick={() => handleActivate(p.id, p.name)}
                    disabled={activatingPlanId === p.id}
                    className="btn"
                    style={{ backgroundColor: '#5a9e6f', borderColor: '#5a9e6f', color: 'white', cursor: 'pointer', width: '100%' }}
                  >
                    {activatingPlanId === p.id ? 'Renewing...' : 'Active (Click to Renew +30 Days)'}
                  </button>
                ) : (
                  <button
                    onClick={() => handleActivate(p.id, p.name)}
                    disabled={activatingPlanId === p.id}
                    className="btn"
                    style={{ cursor: 'pointer', width: '100%' }}
                  >
                    {activatingPlanId === p.id ? 'Activating...' : isAuthenticated ? 'Activate Membership' : 'Sign In to Join'}
                  </button>
                )}
              </div>
            );
          })
        )}
      </section>

      {/* Benefits Grid */}
      <section className="membership-benefits">
        <div className="container">
          <h2 className="section-title">Membership Benefits</h2>
          <p className="section-subtitle">More than just a subscription, a journey to wellness</p>

          <div className="benefits-grid">
            <div className="benefit">
              <h4>Personalized Wellness</h4>
              <p>Tailored treatments designed around your unique health needs and goals.</p>
            </div>
            <div className="benefit">
              <h4>Flexible Scheduling</h4>
              <p>Easy online booking with priority access for members.</p>
            </div>
            <div className="benefit">
              <h4>Exclusive Discounts</h4>
              <p>Guaranteed member savings calculated and applied automatically at checkout.</p>
            </div>
            <div className="benefit">
              <h4>Holistic Experience</h4>
              <p>Full access to all relaxation lounges, herbal teas, and retreat facilities.</p>
            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default MembershipPage;
