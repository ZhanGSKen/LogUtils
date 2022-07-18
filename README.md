# LogUtils

#### 介绍
安卓应用日志显示辅助模块。 

#### 软件架构
使用安卓应用AIDE编译。
app 是测试类库的项目。
liblogutils 是可供外部引用的类库。
类库版本查询网址：
https://jitpack.io/#com.github.zhangsken/LogUtils

#### liblogutils 类库引用方法
(1)修改项目中的上一层根目录build.gradle文件，添加的Maven库:
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
## 修改项目文件夹build.gradle文件，添加项目依赖项
dependencies {
            implementation 'com.github.zhangsken.LogUtils:liblogutils:2.5.2'
    }
    
    
(2)类库Fragment使用
类库使用方法一(分屏模式)：
## 分屏窗口调用步骤
Intent i = new Intent(MainActivity.this, com.github.zhangsken.liblogutils.LogViewActivity.class);
i.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT | Intent.FLAG_ACTIVITY_NEW_TASK);
startActivity(i);

类库使用方法二(Fragment模式)：
## 导入类
import com.github.zhangsken.liblogutils.LogViewFragment;
## Fragment 实现步骤
LogViewFragment mLogViewFragment = new LogViewFragment();
FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
tx.add(R.id.activitymainFrameLayout1, mLogViewFragment, LogViewFragment.TAG);
tx.commit();

(2)类库控件LogView使用
在布局文件xxx.xml直接添加控件，代码如下：
<com.github.zhangsken.liblogutils.LogView
            xmlns:app="http://schemas.android.com/apk/res-auto"
            app:tag="ClassTAG1,ClassTAG2"
            app:level="0"
            app:lastcount="0"
            app:textColor="#FF00FF03"
            app:textIsSelectable="true"
            android:text=""
            android:background="#FF000000"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_weight="1.0"
            android:orientation="vertical"
			android:id="@+id/activitymainLogView1"/>
在控制源码xxx.java添加控制，代码如下：
LogView mLogView;
mLogView = findViewById(R.id.activitymainLogView1);
// 运行时调试显示等级设置，(可在xml文件固定设置，本函数适用于多渠道包发布的区别输出)
mLogView.setLevel(2);
// 开始显示Log.
mLogView.startLog();

备注：设置app:tag设置为"[DEFAULT]"就添加应用包内默认的基本类进行筛选显示。

#### 参与贡献
ZhanGSKen<ZhangShaojian2018@163.com> 主要适配小米9开发版MIUI12.5的安卓系统。

#### 参考文档
