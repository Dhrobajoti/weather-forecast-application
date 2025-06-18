import base64
import io
import json
import matplotlib
import pandas as pd

# Set non-interactive backend before importing pyplot
matplotlib.use('Agg')  # Critical for server environments
import matplotlib.pyplot as plt
from django.http import JsonResponse
from rest_framework.decorators import api_view

@api_view(['POST'])
def plot_graph(request):
    try:
        # 1. Parse JSON data
        weather_data = json.loads(request.body)
        df = pd.DataFrame(weather_data)
        df['date'] = pd.to_datetime(df['date'])

        # 2. Configure plot (non-GUI)
        plt.switch_backend('Agg')  # Redundant safety check
        plt.figure(figsize=(12, 6))
        
        # 3. Generate plot
        plt.style.use('seaborn-v0_8')
        plt.plot(df['date'], df['maxTemp'], 'r-', linewidth=2, label='Max Temp (°C)')
        plt.plot(df['date'], df['minTemp'], 'b-', linewidth=2, label='Min Temp (°C)')
        plt.fill_between(df['date'], df['minTemp'], df['maxTemp'], color='gray', alpha=0.1)
        plt.xlabel('Date')
        plt.ylabel('Temperature (°C)')
        plt.title('Temperature Trend')
        plt.legend()
        plt.xticks(rotation=45)
        plt.tight_layout()

        # 4. Save to buffer
        buffer = io.BytesIO()
        plt.savefig(buffer, format='png', dpi=100, bbox_inches='tight')
        buffer.seek(0)
        plot_base64 = base64.b64encode(buffer.read()).decode('ascii')
        
        # 5. Clean up
        plt.close('all')  # Prevent memory leaks
        buffer.close()

        return JsonResponse({'plot': plot_base64})
        
    except Exception as e:
        # Ensure no dangling figures on error
        plt.close('all')  
        return JsonResponse({'error': str(e)}, status=400)