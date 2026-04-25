# 羊毛猎手 YangMaoHunter

🐑 深圳优惠信息猎人 - 消灭羊毛信息差

自动收集线上线下优惠信息，个性化推荐，每日推送。

## 功能

- ✅ 自动爬虫收集优惠信息
- ✅ 下拉刷新/点击刷新获取最新数据
- ✅ 分类筛选（奶茶/餐饮/银行/网购...）
- ✅ 点击一键跳转领取
- ✅ 即将开抢单独列表
- ✅ 收藏/不喜欢反馈
- ✅ **个性化推荐** - 根据你的浏览习惯学习偏好
- ✅ 每日7点推送优惠汇总（需要配置Cloud Functions）

## 快速开始

### 1. 配置Firebase

1. 打开 [Firebase Console](https://console.firebase.google.com/) 创建项目
2. 添加Android应用，包名: `com.yangmaolie.hunter`
3. 下载 `google-services.json` → 放到 `app/` 目录
4. 开启 **Authentication → 匿名登录**
5. 创建 **Firestore Database**
6. 生成服务账号密钥 → `项目设置 → 服务账号 → 生成私钥`
   → 保存为 `crawler/config/service-account.json`

### 2. 编译APK

这个项目配置了 GitHub Actions 自动编译：

1. Push 到 GitHub 后，自动触发构建
2. 去 `Actions → Latest run → Artifacts` 下载 APK
3. 安装到手机即可

或者本地用Android Studio打开，点击 `Build → Build APK` 直接编译。

### 3. 运行爬虫添加数据

```bash
cd crawler
pip install -r requirements.txt
# 添加数据源（修改sources目录）
python main.py
```

爬完后去Firebase Console把 `deals` 里的 `status` 从 `pending` 改成 `approved` 就能在App看到了。

## 项目结构

- `app/` - Android App Kotlin代码
- `crawler/` - Python爬虫
- `.github/workflows/` - GitHub Actions自动构建APK

## 截图

（待补充）

## 许可证

MIT
