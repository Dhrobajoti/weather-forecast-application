const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const exportRoutes = require('./routes/exportRoutes');

const app = express();
const PORT = 8085;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Routes
app.use('/api/export', exportRoutes);

// Start server
app.listen(PORT, () => {
    console.log(`Exporter service running on port ${PORT}`);
});