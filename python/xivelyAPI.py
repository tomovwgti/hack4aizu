__author__ = 'tomo'
# -*- coding: utf-8 -*-

import xively
import datetime

api = xively.XivelyAPIClient("API-Key")
feed = api.feeds.get(1495248008)
list(feed.datastreams)

# 現在のそれぞれの値
print('%s: ' % feed.updated)
print('%s: ' % feed.datastreams[0].id + '%s ' % feed.datastreams[0].current_value)
print('%s: ' % feed.datastreams[1].id + '%s ' % feed.datastreams[1].current_value)
print('%s: ' % feed.datastreams[2].id + '%s ' % feed.datastreams[2].current_value)
print('%s: ' % feed.datastreams[3].id + '%s ' % feed.datastreams[3].current_value)
print('%s: ' % feed.datastreams[4].id + '%s ' % feed.datastreams[4].current_value)
print('%s: ' % feed.datastreams[5].id + '%s ' % feed.datastreams[5].current_value)
print('%s, ' % feed.location.lat + '%s ' % feed.location.lon)


## 最新から過去の情報を取得
start = datetime.datetime.utcnow()
stream = feed.datastreams[3]  # Pressureの場合
points = stream.datapoints.history(end=start, limit=20, duration='6hours', interval=0)
datalist = list(points)

print('length: %s' % len(datalist))

# リストを表示
for data in datalist:
    print('%s: ' % data.at + '%s ' % data.value + 'hPa')