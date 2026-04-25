from typing import List
from datetime import datetime, timedelta
import requests
from bs4 import BeautifulSoup
from .base import BaseCrawler, CrawledDeal


class ShenzhenYouhuiCrawler(BaseCrawler):
    """深圳优惠网示例爬虫

    这是一个示例，根据实际网站结构调整选择器
    """

    @property
    def source_name(self) -> str:
        return "深圳优惠示例"

    def crawl(self) -> List[CrawledDeal]:
        url = "https://example-sz-youhui.com/deals"  # 替换为实际网址
        headers = {
            "User-Agent": (
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
                "AppleWebKit/537.36 (KHTML, like Gecko) "
                "Chrome/118.0.0.0 Safari/537.36"
            )
        }

        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, "lxml")
        deals = []

        # 根据实际HTML结构修改这里
        for item in soup.select(".deal-item"):
            title = self.clean_text(item.select_one(".title").text)
            description = self.clean_text(item.select_one(".description").text)

            # 解析价格
            price_text = item.select_one(".price").text.strip()
            # 这里需要根据实际格式解析，示例: "¥19.9 原价¥38"
            import re
            prices = re.findall(r"\d+\.?\d*", price_text)
            if len(prices) >= 2:
                deal_price = float(prices[0])
                original_price = float(prices[1])
            else:
                deal_price = float(prices[0]) if prices else 0
                original_price = 0

            discount = int(deal_price / original_price * 100) if original_price > 0 else 0

            # 时间解析
            # 假设显示 "活动时间: 2024-04-23 至 2024-04-30"
            date_text = item.select_one(".date").text
            # 这里需要解析，示例直接用当前时间加几天
            now = datetime.now()
            start_time = int(now.timestamp())
            end_time = int((now + timedelta(days=7)).timestamp())

            # 判断分类
            category = "other"
            if "奶茶" in title or "喜茶" in title or "瑞幸" in title:
                category = "milk_tea"
            elif "餐饮" in title or "火锅" in title or "餐厅" in title:
                category = "restaurant"
            elif "银行" in title or "信用卡" in title:
                category = "bank"
            elif "淘宝" in title or "京东" in title or "拼多多" in title:
                category = "online_shop"

            # 图片
            img_url = item.select_one("img").get("src", "")

            # 链接
            link = item.select_one("a").get("href", "")
            if link and not link.startswith("http"):
                # 补全相对链接
                link = url.rsplit("/", 3)[0] + link

            deal = CrawledDeal(
                title=title,
                description=description,
                category=category,
                original_price=original_price,
                deal_price=deal_price,
                discount=discount,
                start_time=start_time,
                end_time=end_time,
                is_upcoming=False,
                is_online=False,
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

        return deals
