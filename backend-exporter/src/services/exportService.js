const ExcelJS = require('exceljs');
const { createObjectCsvWriter } = require('csv-writer');

const generateCsv = async (data) => {
    const csvWriter = createObjectCsvWriter({
        path: 'temp.csv',
        header: [
            {id: 'fetchTime', title: 'Fetch Time'},
            {id: 'latitude', title: 'Latitude'},
            {id: 'longitude', title: 'Longitude'},
            {id: 'date', title: 'Date'},
            {id: 'maxTemp', title: 'Max Temp (°C)'},
            {id: 'minTemp', title: 'Min Temp (°C)'}
        ]
    });

    await csvWriter.writeRecords(data);
    // In production, use fs.readFileSync() instead
    return require('fs').readFileSync('temp.csv');
};

const generateExcel = async (data) => {
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('Weather Data');
    
    // Add headers
    worksheet.columns = [
        { header: 'Fetch Time', key: 'fetchTime' },
        { header: 'Latitude', key: 'latitude' },
        { header: 'Longitude', key: 'longitude' },
        { header: 'Date', key: 'date' },
        { header: 'Max Temp (°C)', key: 'maxTemp' },
        { header: 'Min Temp (°C)', key: 'minTemp' }
    ];
    
    // Add data
    worksheet.addRows(data);
    
    // Generate buffer
    const buffer = await workbook.xlsx.writeBuffer();
    return buffer;
};

module.exports = {
    generateCsv,
    generateExcel
};