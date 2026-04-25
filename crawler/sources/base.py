from abc import ABC, abstractmethod
from typing import List, Dict, Optional
from dataclasses import dataclass
from datetime import datetime


@dataclass
class CrawledDeal:
    title: str
    description: str
    category: str
    original_price: float
    deal_price: float
    discount: int  # 折扣百分比，比如50就是5折
    start_time: int  # 毫秒时间戳
    end_time: int  # 毫秒时间戳
    is_upcoming: bool
    is_online: bool
    location_lat: Optional[float]
    location_lng: Optional[float]
    address: str
    district: str
    brand_name: str
    usage_rules: List[str]
    action_url: str
    images: List[str]
    cover_image: str
    source: str = "crawler"


class BaseCrawler(ABC):
    """爬虫基类，所有具体爬虫继承这个"""

    @property
    @abstractmethod
    def source_name(self) -> str:
        """爬虫来源名称"""
        pass

    @abstractmethod
    def crawl(self) -> List[CrawledDeal]:
        """执行爬取，返回优惠列表"""
        pass

    def clean_text(self, text: str) -> str:
        """清洗文本"""
        return text.strip().replace("\n", " ").replace("\r", "")
