from typing import List
from datetime import datetime
import requests
import xml.etree.ElementTree as ET
from .base import BaseCrawler, CrawledDeal


class RSSFeedCrawler(BaseCrawler):
    """通用RSS订阅爬虫

    可以添加多个优惠资讯的RSS地址
    """

    # 你可以在这里添加更多RSS源
    RSS_FEEDS = [
        # 示例：添加你的优惠信息RSS源
        # {
        #   "url": "https://example.com/feed",
        #   "name": "优惠资讯站",
        #   "default_category": "other"
        # }
    ]

    @property
    def source_name(self) -> str:
        return "RSS订阅"

    def _guess_category(self, title: str) -> str:
        """根据标题猜分类"""
        title_lower = title.lower()
        if any(k in title_lower for k in ["奶茶", "喜茶", "瑞幸", "coCo", "一点点"]):
            return "milk_tea"
        if any(k in title_lower for k in ["餐饮", "火锅", "餐厅", "烤肉", "汉堡"]):
            return "restaurant"
        if any(k in title_lower for k in ["银行", "信用卡", "权益"]):
            return "bank"
        if any(k in title_lower for k in ["淘宝", "京东", "拼多多", "天猫", "网购"]):
            return "online_shop"
        if any(k in title_lower for k in ["美食", "零食"]):
            return "food_drink"
        return "other"

    def crawl(self) -> List[CrawledDeal]:
        deals = []

        for feed in self.RSS_FEEDS:
            print(f"  抓取RSS: {feed['name']} {feed['url']}")
            try:
                response = requests.get(feed["url"], timeout=10)
                response.raise_for_status()

                root = ET.fromstring(response.content)

                # RSS 2.0 format
                for item in root.findall(".//item"):
                    title = item.find("title").text
                    if not title:
                        continue
                    title = self.clean_text(title)

                    description = item.find("description").text or ""
                    description = self.clean_text(description)

                    link = item.find("link").text or ""

                    # 发布时间
                    pub_date = item.find("pubDate").text
                    start_time = int(datetime.now().timestamp())
                    if pub_date:
                        from email.utils import parsedate_to_datetime
                        try:
                            dt = parsedate_to_datetime(pub_date)
                            start_time = int(dt.timestamp())
                        except:
                            pass

                    # 默认一周后结束
                    from datetime import timedelta
                    end_time = int((datetime.now() + timedelta(days=7)).timestamp())

                    category = feed.get("default_category", self._guess_category(title))

                    # 提取图片，RSS中通常在description里
                    import re
                    img_url = ""
                    matches = re.search(r'<img[^>]+src="([^">]+)"', description)
                    if matches:
                        img_url = matches.group(1)

                    deal = CrawledDeal(
                        title=title,
                        description=description,
                        category=category,
                        original_price=0,
                        deal_price=0,
                        discount=0,
                        start_time=start_time,
                        end_time=end_time,
                        is_upcoming=False,
                        is_online=True,
                        location_lat=None,
                        location_lng=None,
                        address="",
                        district="",
                        brand_name="",
                        usage_rules=[],
                        action_url=link,
                        images=[img_url] if img_url else [],
                        cover_image=img_url if img_url else "",
                    )
                    deals.append(deal)

            except Exception as e:
                print(f"    抓取失败: {e}")
                continue

        return deals
