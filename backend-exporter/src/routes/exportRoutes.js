const express = require('express');
const router = express.Router();
const exportService = require('../services/exportService');

router.post('/csv', async (req, res) => {
    try {
        const csvData = await exportService.generateCsv(req.body);
        res.setHeader('Content-Type', 'text/csv');
        res.setHeader('Content-Disposition', 'attachment; filename=weather_data.csv');
        res.send(csvData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

router.post('/excel', async (req, res) => {
    try {
        const excelData = await exportService.generateExcel(req.body);
        res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        res.setHeader('Content-Disposition', 'attachment; filename=weather_data.xlsx');
        res.send(excelData);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;