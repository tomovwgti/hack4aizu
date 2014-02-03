__author__ = 'tomo'
# -*- coding: utf-8 -*-

import requests
import json

res = requests.get('https://api.xively.com//v2/feeds/1495248008.json', headers={'X-ApiKey':'API-Key'})
response = res.json()

print("status %s" % res.status_code)
print("contents %s" % response)
print(json.dumps(response, sort_keys=True, indent=4))

print("Humidity %s" % response['datastreams'][0]['current_value'])
print("Illumination %s" % response['datastreams'][1]['current_value'])
print("Motion %s" % response['datastreams'][2]['current_value'])
print("Pressure %s" % response['datastreams'][3]['current_value'])
print("Sound %s" % response['datastreams'][4]['current_value'])
print("Temperature %s" % response['datastreams'][5]['current_value'])
print("location %s" % response['location']['name'])
print("location lat %s" % response['location']['lat'])
print("location lon %s" % response['location']['lon'])
