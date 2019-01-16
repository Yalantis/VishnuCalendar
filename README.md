# Kalendar-Android

![Preview](https://github.com/p1nkydev/Sample/blob/master/demo_collapse_expand.gif)
![Preview](https://github.com/p1nkydev/Sample/blob/master/demo_swipe.gif)

## Requirements
- Android SDK 17+

## Usage

Add to your root build.gradle:
```Groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency:
```Groovy
dependencies {
	implementation 'com.github.Yalantis:Kalendar:v0.0.1-alpha'
}
```

## How to use this library in your project?

First of all, add `Kalendar` to the xml layout of your activity, remember, you can specify any width but height always will be wrap_content. So it looks like that:

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.yalantis.view.Kalendar
	android:id="@+id/kalendar"
	android:layout_width="match_parent"
	android:layout_height="wrap_content" />
```

## Customization

For customization ui Kalendar you can use such attributes value :

To change text in drag area you can use
```xml
<com.yalantis.kalendar.view.Kalendar
	...
	app:dragText="Sample"
	...
```

To change height of drag area you need to set value to dragHeight in dp. The default value for dragHeight is 32dp.
```xml
 <com.yalantis.kalendar.view.Kalendar
 	...	
        app:dragHeight="16dp" 
        ...
/>
```
Change drag text size you can set value to dragTextSize in sp. The default value for dragTextSize is 12sp
```xml
<com.yalantis.kalendar.view.Kalendar
 	...	
        app:dragTextSize="16sp" 
        ...
/>
```

Change color of text in drag area you need to set value to dragTextColor as a reference to the color. The default value for dragTextColor is @color/drag_text_color which is #ababab
```xml
<com.yalantis.kalendar.view.Kalendar
 	...	
        app:dragTextColor="@android:color/black" 
        ...
/>
```

Also you can change drag area background color by simply use dragColor attribute. The default color is @color/light_gray which is #dcdfe1
```xml
<com.yalantis.kalendar.view.Kalendar
 	...	
        app:dragColor="@android:color/black" 
        ...
/>
```

Kalendar has collapse() and expand() methods which collapse/expand current selected month
```Kotlin
kalendar.collapse()
kalendar.expand()
```

Kalendar has getMonthAt(position: Int) method which returns month from view pager's adapter by position argument
```Kotlin
kalendar.getMonthAt(position: Int) 
```

Also, you can realize automatic swipe using swipeMonth(isSwipeToEnd: Boolean) method:
```Kotlin
kalendar.swipeMonth(false) - swipe to left
kalendar.swipeMonth(true) - swipe to right
```

Also you can create `KalendarListener` and use all the methods of the `KalendarListener`. This interface provides empty implementations of the methods. For any custom animation callback handle you can create any custom listener that cares only about a subset of the methods of this listener can simply implement the interface directly:
```
fun onDayClick(date: Date)
fun onStateChanged(isCollapsed: Boolean)
fun onHeightChanged(newHeight: Int)
fun onMonthChanged(forward: Boolean, date: Date? = null)
fun onSizeMeasured(monthPage: MonthPage, collapsedHeight: Int, totalHeight: Int)
```

## Let us know!

We’d be really happy if you sent us links to your projects where you use our component. Just send an email to github@yalantis.com And do let us know if you have any questions or suggestion regarding the animation. 

## License

	The MIT License (MIT)

	Copyright © 2019 Yalantis, https://yalantis.com

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.