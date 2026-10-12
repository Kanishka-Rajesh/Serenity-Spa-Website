import React from 'react';

const Footer: React.FC = () => {
  return (
    <footer>
      <div className="container">
        <div className="footer-content">
          <div className="footer-column">
            <h3>Serenity Spa</h3>
            <p>123 Tranquility Lane</p>
            <p>Wellness Heights, CA 90210</p>
            <p>Phone: (555) 123-4567</p>
            <p>Email: info@serenityspa.com</p>
          </div>

          <div className="footer-column">
            <h3>Hours</h3>
            <p>Monday - Friday: 9am - 9pm</p>
            <p>Saturday: 8am - 10pm</p>
            <p>Sunday: 10am - 8pm</p>
          </div>

          <div className="footer-column">
            <h3>Our Location</h3>
            <iframe
              src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3306.5832685668516!2d-118.24281798468268!3d34.052234380610414!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x80c2b92b40697505%3A0x9a3153e8b1c93b15!2sLos%20Angeles%2C%20CA!5e0!3m2!1sen!2sus!4v1678912345678!5m2!1sen!2sus"
              width="100%"
              height="200"
              style={{ border: 0 }}
              allowFullScreen={true}
              loading="lazy"
              title="Serenity Spa Location"
            ></iframe>
          </div>
        </div>

        <div className="copyright">
          <p>&copy; 2025 Serenity Spa &amp; Wellness Retreat. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
