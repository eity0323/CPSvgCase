# AIManager
this is a svg demo.

#Function description
Done:
1、 dynamic draw svg path(Custom Svg View)

<img src="/screenshot/1.gif" alt="dynamic draw svg path" title="dynamic draw svg path" width="200" height = "400" />

2、display static svg file(Custom Svg View)
3、display svg file(from assets、raw、file in strorage) with ImageView using a ImageLoader library
4、display svg url with ImageView using retrofit library
5、convert svg file to bitmap
6、combine multiply svg file

TODO:
1、custom svg animation
2、custom svg filter

# Structure introduce

- MainActivity.java
entrance of application,test the functions above.

- utils/*
util class for dealing with business

- network
util class for request data from network

- DynamicSvgPathView.java
dynamic draw svg

- DynamicSvgView.java
display static svg file

- DynamicSvgUtils.java
help to parse svg xml





