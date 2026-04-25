#!/usr/bin/env python3
"""
羊毛猎手 爬虫主入口
运行: python main.py
"""

import sys
import os

# 添加项目根目录到路径
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from utils.firebase_client import FirebaseClient
from processors.dedupe import Deduplicator
from sources.base import BaseCrawler, CrawledDeal

# 导入所有爬虫
from sources import *

import importlib
import pkgutil


def get_all_crawlers() -> list[BaseCrawler]:
    """自动发现所有爬虫模块"""
    crawlers = []
    package = sources
    for _, name, _ in pkgutil.iter_modules(package.__path__):
        module = importlib.import_module(f"sources.{name}")
        for member_name in dir(module):
            member = getattr(module, member_name)
            if (
                isinstance(member, type)
                and issubclass(member, BaseCrawler)
                and member != BaseCrawler
            ):
                crawlers.append(member())
    return crawlers


def main():
    print("=" * 50)
    print("羊毛猎手爬虫")
    print("=" * 50)

    # 初始化Firebase
    try:
        fb = FirebaseClient()
        print("✓ Firebase连接成功")
    except Exception as e:
        print(f"✗ Firebase连接失败: {e}")
        print("\n请先下载Firebase service account密钥:")
        print("1. 打开Firebase Console → 项目设置 → 服务账号")
        print("2. 生成新的私钥，下载保存为 config/service-account.json")
        return 1

    # 获取所有爬虫
    crawlers = get_all_crawlers()
    if not crawlers:
        print("未找到任何爬虫，请在sources目录添加爬虫文件")
        return 1

    print(f"找到 {len(crawlers)} 个爬虫: {', '.join(c.source_name for c in crawlers)}")

    # 去重
    deduplicator = Deduplicator()

    total_deals = 0
    total_saved = 0

    # 运行每个爬虫
    for crawler in crawlers:
        print(f"\n--- 运行爬虫: {crawler.source_name} ---")
        try:
            deals = crawler.crawl()
            deals = deduplicator.clean(deals)
            print(f"  抓取到 {len(deals)} 条优惠")

            saved = fb.save_multiple(deals)
            print(f"  新增 {saved} 条到Firebase")

            total_deals += len(deals)
            total_saved += saved

        except Exception as e:
            print(f"  爬虫运行出错: {e}")
            import traceback
            traceback.print_exc()

    print("\n" + "=" * 50)
    print(f"爬取完成: 总共 {total_deals} 条，新增 {total_saved} 条")
    print("=" * 50)
    print("\n请注意: 所有新抓取的优惠状态都是 pending，")
    print("请到 Firebase Console 审核后改为 approved 才能在App中显示")

    return 0


if __name__ == "__main__":
    sys.exit(main())
