# 羊毛猎手 爬虫

## 使用方法

### 1. 配置Firebase凭证

1. 打开 [Firebase Console](https://console.firebase.google.com/)
2. 选择你的项目 → 项目设置 → 服务账号
3. 点击「生成新的私钥」，下载文件
4. 将下载的文件保存为 `config/service-account.json`

### 2. 安装依赖

```bash
cd crawler
pip install -r requirements.txt
```

### 3. 添加数据源

在 `sources/` 目录下添加爬虫：

- 参考 `sz_youhui_example.py` 写HTML爬取
- 或者在 `rss_feed.py` 中的 `RSS_FEEDS` 添加RSS订阅源

### 4. 运行爬虫

```bash
python main.py
```

### 5. 审核

爬虫抓取的优惠默认状态是 `pending`，需要你到 Firebase Console → Firestore Database，找到 `deals` 集合，把状态 `status` 从 `pending` 改为 `approved` 才会在App中显示。

## 项目结构

```
crawler/
├── main.py              # 主入口
├── requirements.txt     # Python依赖
├── sources/
│   ├── base.py          # 爬虫基类
│   ├── __init__.py
│   ├── sz_youhui_example.py  # 示例HTML爬虫
│   └── rss_feed.py      # 通用RSS订阅爬虫
├── processors/
│   └── dedupe.py        # 去重
├── utils/
│   └── firebase_client.py  # Firebase客户端
└── config/
    └── .gitkeep         # 放service-account.json
```

## 定时运行（可选）

如果你想要每天自动爬取，可以配置cron job：

```
# 每天早上6点运行，结果输出日志
0 6 * * * cd /path/to/YangMaoHunter/crawler && /usr/bin/python main.py >> crawler.log 2>&1
```
