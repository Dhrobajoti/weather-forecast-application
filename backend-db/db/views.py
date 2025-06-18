from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import WeatherData
import json

@csrf_exempt
def save_weather_data(request):
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
            saved_records = []
            
            for item in data:
                record = WeatherData.objects.create(
                    fetch_time=item['fetchTime'],
                    latitude=item['latitude'],
                    longitude=item['longitude'],
                    date=item['date'],
                    max_temp=item['maxTemp'],
                    min_temp=item['minTemp']
                )
                saved_records.append(record.id)
            
            return JsonResponse({
                'status': 'success',
                'saved_records': len(saved_records)
            })
            
        except Exception as e:
            return JsonResponse({
                'status': 'error',
                'message': str(e)
            }, status=400)
    
    return JsonResponse({
        'status': 'error',
        'message': 'Only POST requests allowed'
    }, status=405)