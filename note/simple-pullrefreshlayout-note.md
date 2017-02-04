# SimplePullRefreshLayout Note

A very simple, less features and easy to understand PullRefreshLayout, likes SwipeRefreshLayout, but much simple.

## References

1. [Android Scroller 完全解析，关于 Scroller 你所需知道的一切](http://blog.csdn.net/guolin_blog/article/details/48719871)
2. [Yalantis Phoenix](https://github.com/Yalantis/Phoenix)

## Samples

![](./art/1_sample.gif)

## Note

从郭霖的[Android Scroller 完全解析，关于 Scroller 你所需知道的一切](http://blog.csdn.net/guolin_blog/article/details/48719871)这篇文章得到启发，它讲解了如何使用 Scroller 来实现一个简单版的 ViewPager，把 ViewPager 竖过来不就类似 PullRefreshLayout 了吗，于是参考它的代码，用 scrollTo/scrollBy/scroller 实现了一个功能比较简单的 SimplePullRefreshLayout，代码很少而且很简单，很好理解，使用方法和 SwipeRefreshLayout 相似。如下所示：

XML:

    <com.baurine.simplepullrefreshlayout.SimplePullRefreshLayout
        android:id="@+id/pull_refresh_layout"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#000000"
        app:extra_height="20dp">

        <!-- you can replace this default SimpleRefreshStateView by yourself implementation
        <com.baurine.simplepullrefreshlayout.SimpleRefreshStateView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"/>
        -->

        <android.support.v7.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    </com.baurine.simplepullrefreshlayout.SimplePullRefreshLayout>

Java Code:

    private void initViews() {
      ...
      pullRefreshLayout = (SimplePullRefreshLayout) findViewById(R.id.pull_refresh_layout);
      pullRefreshLayout.setOnRefreshListener(new SimplePullRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
              pullRefreshLayout.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      pullRefreshLayout.setRefreshing(false);
                  }
              }, 2000);
          }
      });
    }

    public void onClick(View view) {
      pullRefreshLayout.setRefreshing(true);
      pullRefreshLayout.postDelayed(new Runnable() {
          @Override
          public void run() {
              pullRefreshLayout.setRefreshing(false);
          }
      }, 2000);
    }

SimplePullRefreshLayout 内部有一个默认的用来显示状态的 SimpleRefreshStateView，但用户可以实现自己的 RefreshStateView，用来替代默认实现，只要把它作为 SimplePullRefreshLayout 的第一个 child view 就行。自定义的 RefreshStateView 只需要实现 RefreshStateView 接口就行：

    public interface RefreshStateView {
        void setPercent(float percent);

        void setRefreshing(boolean refreshing);
    }

### 实现思路

1. 在 layout 时，把 RefreshStateView 隐藏在 RecyclerView 上面，初始化时看不到；
1. 在 onInterceptTouchEvent 中监控向下的滑动动作，如果向下的滑动距离超过 touchSlop，则拦截之；(处理滑动冲突主要靠 onInterceptTouchEvent)
1. 拉截以后，触摸事件会跳过 onInterceptTouchEvent，直接交给 onTouchEvent 处理。
1. 我们在 onTouchEvent 中处理 `ACTION_MOVE` 动作，使用 scrollBy 方法，随着手指的向下滑动，控制 SimplePullRefreshLayout 也向下滚动其中的内容，这时，之前隐藏在顶部的 RefreshStateView 就会逐渐显示出来；
1. RefreshStateView 根据自身显示出来的比例，控制内部的 View 以呈现不同的状态；
1. 当手指弹起时，即处理 `ACTION_UP` 动作，如果滑动的距离超过了 RefreshStateView 的阈值高度，那么 RefreshStateView 将呈现 loading 状态，并且滚动距离悬停在阈值高度；如果没有超过，那么 RefreshStateView 将隐藏。这两种情况都将使用 scroller 来实现平滑的滚动。

### 对自定义 View/ViewGroup 的总结

此次练习再次加深了对 Android 自定义 View 的理解。以下的是一些自己的粗略总结。

广义的自定义 View 分 2 种：

1. 狭义的自定义 View
1. 自定义 ViewGroup

广义的自定义 View 需要实现的方法分 2 大类：一类是处理触摸事件，一类是 尺寸计算/布局/绘制。

    - dispatchTouchEvent()
    - onInterceptTouchEvent()  --> ViewGroup 独有
    - onTouchEvent()
    - ---------------------
    - onMeasure()
    - onLayout()               --> ViewGroup 独有
    - onDraw()                 --> 一般用于 View

狭义的自定义 View 由于是最底层的 View，所以不需要处理 ViewGroup 才有的 onInterceptTouchEvent()，也不需要处理 onLayout()。

自定义 ViewGroup 侧重于事件处理和 onLayout()，而狭义的自定义 View 侧重于 onDraw()。

自定义 ViewGroup 的种类：

1. 简单的组合式 ViewGroup，直接继承自某种容器 ViewGroup，然后在构造函数中直接 inflate 某个 layout。
1. 简单的组合式 ViewGroup，直接继承自某种容器 ViewGroup，然后在 xml 往此 ViewGroup 中填充多个子 View，然后在此 ViewGroup 的 onFinishInflate() 回调中 findViewById()，把这些子 View 找出来。
1. 复杂，继承自 ViewGroup，重载 onInterceptTouchEvent()，onTouchEvent()，onLayout() 等方法。

狭义的自定义 View 的种类：

1. 简单类，直接继承自已有的 View，如 TextView，只做简单的功能扩展。
1. 复杂类，视功能而定，重写 onMeasure() 和 onDraw()，尤其是 onDraw()；另外，视是否要处理触摸事件而是否重载 onTouchEvent()。

onDraw()：

1. canvas 操作：drawLine，drawOval，drawPath，canvas 变换 ...
1. Paint：Shader，Xfermode ...
1. 属性动画：ValueAnimator，ObjectAnimator，view.animate()，插值器，估值器 ...

View 的滑动：

1. 内部：scrollTo / scrollBy / scroller
1. 外部：动画，布局参数 ...

scrollTo, scrollBy 是用于移动 ViewGroup 内部的内容，但 ViewGroup 实际自己的位置是不移动的。
而要是想移动 ViewGroup 自身，那就用属性动画等其它方法。

移动 View (包括内部、外部)：

1. scrollTo, scrollBy, scroller
1. margin (leftMargin, topMargin …)
1. View.layout
1. translationX, translationY
1. View.offsetTopAndBottom --> View.getTop()
1. ...
