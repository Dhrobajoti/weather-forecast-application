from django.db import models

class WeatherData(models.Model):
    fetch_time = models.CharField(max_length=100)
    latitude = models.CharField(max_length=50)
    longitude = models.CharField(max_length=50)
    date = models.CharField(max_length=50)
    max_temp = models.FloatField()
    min_temp = models.FloatField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-created_at']