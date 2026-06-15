# -*- coding: utf-8 -*-
"""フリマ社内システムの画面遷移図を draw.io (.drawio / mxGraph XML) で生成する。"""

NODES = []  # (id, label, x, y, w, h, fill, stroke)
EDGES = []  # (source, target, label, style_extra)

def node(nid, label, x, y, w=170, h=56, fill="#dae8fc", stroke="#6c8ebf"):
    NODES.append((nid, label, x, y, w, h, fill, stroke))

def edge(s, t, label="", extra=""):
    EDGES.append((s, t, label, extra))

# 色
EMP   = ("#dae8fc", "#6c8ebf")  # 社員側（青）
EMPD  = ("#d5e8d4", "#82b366")  # 物品/取引（緑）
ADMIN = ("#ffe6cc", "#d79b00")  # 管理者側（橙）
GATE  = ("#f8cecc", "#b85450")  # ログイン（赤）

# ===== タイトル / 区分ラベル =====
node("title", "フリマ社内システム　画面遷移図", 40, 16, 760, 30, "none", "none")
node("lbl_emp",   "■ 社員側", 40, 60, 120, 24, "none", "none")
node("lbl_admin", "■ 管理者側", 40, 560, 130, 24, "none", "none")

# ===== 社員側 =====
node("login",  "ログイン画面\n(BASE-101 / log-001)", 40,  100, 170, 56, *GATE)
node("menu",   "メニュー・物品一覧\n(item-101)",       260, 100, 170, 56, *EMP)
node("search", "検索結果\n(item-102)",                 500, 90,  170, 56, *EMP)
node("detail", "物品詳細閲覧\n(item-107)",             740, 60,  170, 56, *EMPD)
node("auction","物品詳細(オークション)\n(item-111)",    980, 60,  180, 56, *EMPD)

node("post",   "出品画面\n(item-103)",                 500, 180, 170, 56, *EMP)
node("confirm","出品情報確認\n(item-109)",             740, 180, 170, 56, *EMP)

node("msglist","メッセージ一覧\n(item-104)",           500, 270, 170, 56, *EMP)
node("msgbody","メッセージ内容\n(item-105 / BASE-105)",740, 270, 180, 56, *EMP)

node("mypage", "マイページ\n(item-106)",               500, 360, 170, 56, *EMP)
node("qr",     "譲渡完了登録 /\nQRコード状況確認",       740, 360, 180, 56, *EMPD)

# 社員側エッジ
edge("login","menu","ログイン")
edge("menu","login","ログアウト")
edge("menu","search","検索")
edge("search","detail","選択／クリック")
edge("menu","detail","商品クリック")
edge("detail","auction","オークション物品")
edge("menu","post","出品")
edge("post","confirm","確認")
edge("confirm","post","編集")
edge("confirm","menu","出品確定")
edge("menu","msglist","メッセージ")
edge("msglist","msgbody","クリック")
edge("menu","mypage","マイページ")
edge("qr","msgbody","状況連携")
edge("detail","msgbody","メッセージ送信")

# ===== 管理者側 =====
node("alogin", "管理者ログイン\n(log-002)",            40,  600, 170, 56, *GATE)
node("a701",   "管理者メニュー /\n社員情報管理 (701)",  260, 600, 180, 56, *ADMIN)

node("a702",   "パスワードリセット\n(702)",            520, 500, 160, 50, *ADMIN)
node("a709",   "利用制限\n(709)",                      520, 560, 160, 50, *ADMIN)
node("a703",   "カテゴリ管理\n(703)",                  520, 620, 160, 50, *ADMIN)
node("a704",   "検索キー管理\n(704)",                  520, 680, 160, 50, *ADMIN)

node("a706",   "NGキーワード登録\n(706)",              720, 500, 160, 50, *ADMIN)
node("a710",   "統計情報一覧\n(710)",                  720, 560, 160, 50, *ADMIN)
node("a705",   "不適切コンテンツ監視\n(705)",          720, 640, 180, 56, *ADMIN)

node("a707",   "物品削除\n(707)",                      960, 610, 150, 50, *ADMIN)
node("a708",   "メッセージ削除\n(708)",                960, 680, 150, 50, *ADMIN)

# 管理者側エッジ
edge("alogin","a701","ログイン")
edge("a701","a702","編集")
edge("a701","a709","利用制限")
edge("a701","a703","カテゴリ管理")
edge("a701","a704","検索キー管理")
edge("a701","a706","NGキーワード")
edge("a701","a710","統計情報")
edge("a701","a705","監視")
edge("a705","a707","物品削除")
edge("a705","a708","メッセージ削除")

# ===== XML 生成 =====
def esc(s):
    return (s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
             .replace('"',"&quot;").replace("\n","&#10;"))

cells = []
for nid,label,x,y,w,h,fill,stroke in NODES:
    if fill == "none":
        style = f"text;html=1;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;fontStyle=1;fontSize={16 if nid=='title' else 13};"
    else:
        style = (f"rounded=1;whiteSpace=wrap;html=1;fillColor={fill};strokeColor={stroke};"
                 f"fontSize=11;arcSize=12;")
    cells.append(
        f'        <mxCell id="{nid}" value="{esc(label)}" style="{style}" vertex="1" parent="1">\n'
        f'          <mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry" />\n'
        f'        </mxCell>')

for i,(s,t,label,extra) in enumerate(EDGES):
    style = ("edgeStyle=orthogonalEdgeStyle;rounded=1;html=1;endArrow=block;"
             "jettySize=auto;orthogonalLoop=1;fontSize=10;" + extra)
    cells.append(
        f'        <mxCell id="e{i}" value="{esc(label)}" style="{style}" edge="1" parent="1" source="{s}" target="{t}">\n'
        f'          <mxGeometry relative="1" as="geometry" />\n'
        f'        </mxCell>')

xml = (
'<mxfile host="app.diagrams.net" type="device">\n'
'  <diagram name="画面遷移図" id="furima-flow">\n'
'    <mxGraphModel dx="1200" dy="800" grid="1" gridSize="10" guides="1" tooltips="1" '
'connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="1169" pageHeight="826" '
'math="0" shadow="0">\n'
'      <root>\n'
'        <mxCell id="0" />\n'
'        <mxCell id="1" parent="0" />\n'
+ "\n".join(cells) + "\n"
'      </root>\n'
'    </mxGraphModel>\n'
'  </diagram>\n'
'</mxfile>\n'
)

import os
os.makedirs('/home/user/system/docs/design', exist_ok=True)
out = '/home/user/system/docs/design/画面遷移図_フリマ社内システム.drawio'
with open(out, 'w', encoding='utf-8') as f:
    f.write(xml)
print('saved:', out)
print('nodes:', len(NODES), 'edges:', len(EDGES))
