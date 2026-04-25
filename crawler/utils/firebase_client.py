import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import os
from typing import Optional
from ..sources.base import CrawledDeal


class FirebaseClient:
    def __init__(self):
        # 初始化Firebase
        # 需要下载service-account.json放到config目录
        cred_path = self._get_cred_path()
        if not firebase_admin._apps:
            cred = credentials.Certificate(cred_path)
            firebase_admin.initialize_app(cred)

        self.db = firestore.client()

    def _get_cred_path(self) -> str:
        # 优先从环境变量获取路径，否则默认位置
        cred_path = os.environ.get("FIREBASE_CREDENTIALS")
        if cred_path and os.path.exists(cred_path):
            return cred_path

        default_path = os.path.join(
            os.path.dirname(__file__), "../../config/service-account.json"
        )
        if os.path.exists(default_path):
            return default_path

        raise FileNotFoundError(
            "Firebase service account credentials not found. "
            "Please download from Firebase Console and place at config/service-account.json"
        )

    def check_duplicate(self, title: str, start_time: int) -> bool:
        """检查是否已经存在相同标题相同时间的优惠，去重"""
        deals_ref = self.db.collection("deals")
        query = (
            deals_ref
            .where("title", "==", title)
            .where("startTime", "==", start_time)
            .limit(1)
        )
        results = query.get()
        return len(list(results)) > 0

    def save_deal(self, crawled: CrawledDeal) -> bool:
        """保存优惠到Firebase"""
        try:
            # 去重检查
            if self.check_duplicate(crawled.title, crawled.start_time):
                print(f"Duplicate found: {crawled.title}, skipping")
                return False

            deal_data = {
                "title": crawled.title,
                "description": crawled.description,
                "category": crawled.category,
                "originalPrice": crawled.original_price,
                "dealPrice": crawled.deal_price,
                "discount": crawled.discount,
                "publishTime": int(crawled.start_time * 1000) if crawled.start_time else int(datetime.now().timestamp() * 1000),
                "startTime": crawled.start_time * 1000 if crawled.start_time else int(datetime.now().timestamp() * 1000),
                "endTime": crawled.end_time * 1000,
                "isUpcoming": crawled.is_upcoming,
                "isOnline": crawled.is_online,
                "address": crawled.address,
                "district": crawled.district,
                "brandName": crawled.brand_name,
                "usageRules": crawled.usage_rules,
                "actionUrl": crawled.action_url,
                "images": crawled.images,
                "coverImage": crawled.cover_image,
                "viewCount": 0,
                "clickCount": 0,
                "collectCount": 0,
                "popularity": 50.0,
                "source": crawled.source,
                "status": "pending",  # 需要审核才能显示
                "createdAt": int(datetime.now().timestamp() * 1000),
                "updatedAt": int(datetime.now().timestamp() * 1000),
            }

            if crawled.location_lat and crawled.location_lng:
                from google.cloud.firestore import GeoPoint
                deal_data["location"] = GeoPoint(crawled.location_lat, crawled.location_lng)

            self.db.collection("deals").add(deal_data)
            print(f"Saved new deal: {crawled.title}")
            return True

        except Exception as e:
            print(f"Error saving deal {crawled.title}: {e}")
            return False

    def save_multiple(self, deals: list[CrawledDeal]) -> int:
        """批量保存，返回成功保存的数量"""
        saved = 0
        for deal in deals:
            if self.save_deal(deal):
                saved += 1
        return saved
