from typing import List
from ..sources.base import CrawledDeal


class Deduplicator:
    """去重处理器"""

    def __init__(self):
        self.seen = set()

    def clean(self, deals: List[CrawledDeal]) -> List[CrawledDeal]:
        unique_deals = []
        for deal in deals:
            # 使用标题和开始时间作为key
            key = f"{deal.title.lower()}_{deal.start_time}"
            if key not in self.seen:
                self.seen.add(key)
                unique_deals.append(deal)
        return unique_deals
