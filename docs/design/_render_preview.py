# -*- coding: utf-8 -*-
"""gen_drawio5 と同じ座標・ポート指定でPNGプレビュー（くし形/ポート対応）。"""
from PIL import Image, ImageDraw, ImageFont
import re, math

ns={}
exec(open("/tmp/gen_drawio5.py",encoding="utf-8").read(), ns)

FONT="/usr/share/fonts/opentype/ipafont-gothic/ipag.ttf"
f_box=ImageFont.truetype(FONT,17); f_lbl=ImageFont.truetype(FONT,16); f_title=ImageFont.truetype(FONT,22)

def ports(extra):
    g=lambda k:(float(re.search(k+r'=([0-9.]+)',extra).group(1)) if re.search(k+r'=([0-9.]+)',extra) else None)
    return g('exitX'),g('exitY'),g('entryX'),g('entryY')

def center(n): nid,l,x,y,w,h,s=n; return (x+w/2,y+h/2)
def port(n,px,py):
    nid,l,x,y,w,h,s=n; return (x+px*w, y+py*h)

def best(s,t):
    sx,sy=center(s); tx,ty=center(t); dx,dy=tx-sx,ty-sy
    if abs(dy)>=abs(dx): return (0.5,1,0.5,0) if dy>0 else (0.5,0,0.5,1)
    return (1,0.5,0,0.5) if dx>0 else (0,0.5,1,0.5)

def orient(px,py): return 'h' if px in (0.0,1.0) else 'v'

def route(p0,p1,o0,o1,comb=False):
    x0,y0=p0; x1,y1=p1
    if o0=='v' and o1=='v':
        by=(y0+40) if comb else (y0+y1)/2
        return [p0,(x0,by),(x1,by),p1]
    if o0=='h' and o1=='h':
        bx=(x0+x1)/2
        return [p0,(bx,y0),(bx,y1),p1]
    if o0=='v' and o1=='h':
        return [p0,(x0,y1),p1]
    return [p0,(x1,y0),p1]

def arrow(d,a,b,col):
    x0,y0=a;x1,y1=b;ang=math.atan2(y1-y0,x1-x0);L=12;w=0.5
    d.polygon([(x1,y1),(x1-L*math.cos(ang-w),y1-L*math.sin(ang-w)),(x1-L*math.cos(ang+w),y1-L*math.sin(ang+w))],fill=col)

def dline(d,a,b,col,dash=False):
    if not dash: d.line([a,b],fill=col,width=2); return
    x0,y0=a;x1,y1=b;dist=math.hypot(x1-x0,y1-y0);n=max(1,int(dist/14))
    for k in range(n):
        if k%2==0:
            d.line([(x0+(x1-x0)*k/n,y0+(y1-y0)*k/n),(x0+(x1-x0)*(k+1)/n,y0+(y1-y0)*(k+1)/n)],fill=col,width=2)

def render(nodes,edges,pw,ph,path):
    S=2; img=Image.new("RGB",(pw*S,ph*S),"white"); d=ImageDraw.Draw(img)
    nm={n[0]:n for n in nodes}; sc=lambda p:(p[0]*S,p[1]*S)
    for (s,t,label,extra) in edges:
        S0,T0=nm[s],nm[t]
        ex,ey,nx,nyy=ports(extra)
        if ex is None: ex,ey,nx,nyy=best(S0,T0)
        p0=port(S0,ex,ey); p1=port(T0,nx,nyy)
        comb = (s in ('d1','d2'))
        pts=route(p0,p1,orient(ex,ey),orient(nx,nyy),comb)
        dash="dashed=1;" in extra; col=(110,110,110) if dash else (0,0,0)
        for i in range(len(pts)-1): dline(d,sc(pts[i]),sc(pts[i+1]),col,dash)
        if "endArrow=none;" not in extra: arrow(d,sc(pts[-2]),sc(pts[-1]),col)
        if label:
            if comb:  # 枝先の真上にラベル
                lx,ly=sc((p1[0], p1[1]-20))
            else:
                # 経路中点
                mid=pts[len(pts)//2]; lx,ly=sc(mid)
            tb=d.textbbox((0,0),label,font=f_lbl); tw=tb[2]-tb[0]; th=tb[3]-tb[1]
            x=lx-tw/2; y=ly-th/2
            d.rectangle([x-5,y-3,x+tw+5,y+th+3],fill="white",outline=(170,170,170))
            d.text((x,y-tb[1]),label,fill=(0,0,0),font=f_lbl)
    for n in nodes:
        nid,label,x,y,w,h,style=n; X,Y,Wd,Ht=x*S,y*S,w*S,h*S
        if "rhombus" in style:
            d.polygon([(X+Wd/2,Y),(X+Wd,Y+Ht/2),(X+Wd/2,Y+Ht),(X,Y+Ht/2)],outline="black",fill="white",width=2)
        elif "swimlane" in style:
            d.rectangle([X,Y,X+Wd,Y+Ht],outline="black",fill="white",width=2)
            hd=30*S; d.line([(X,Y+hd),(X+Wd,Y+hd)],fill="black",width=2)
            tb=d.textbbox((0,0),label,font=f_box); tw=tb[2]-tb[0]
            d.text((X+(Wd-tw)/2,Y+(hd-(tb[3]-tb[1]))/2-tb[1]),label,fill="black",font=f_box)
        else:
            d.rectangle([X,Y,X+Wd,Y+Ht],outline="black",fill="white",width=2)
            d.text((X+8,Y+8),label,fill="black",font=f_title)
    img.save(path); print("saved",path,img.size)

import importlib
# pw2 を再計算
order=ns['order']; x0=ns['x0']; gap=ns['gap']; W=ns['W']
pw2=x0+(len(order)-1)*gap+W+120
render(ns["n1"],ns["e1"],1700,1200,"/home/user/system/docs/design/画面遷移図_社員側.png")
render(ns["n2"],ns["e2"],pw2,1000,"/home/user/system/docs/design/画面遷移図_管理者側.png")
