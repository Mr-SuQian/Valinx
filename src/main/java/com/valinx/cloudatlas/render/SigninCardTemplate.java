package com.valinx.cloudatlas.render;

import com.valinx.cloudatlas.service.SigninService;

/**
 * 签到卡片 HTML 模板构造器
 */
public class SigninCardTemplate {

    public static String build(SigninService.SigninResult result) {
        // TODO: Port the full CSS and HTML from renderer.js
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { margin: 0; background: transparent; font-family: 'Segoe UI', sans-serif; }
                        .main-container {
                            width: 500px;
                            padding: 30px;
                            background: linear-gradient(135deg, #1e293b, #0f172a);
                            color: white;
                            border-radius: 20px;
                            border: 1px solid rgba(255, 255, 255, 0.1);
                        }
                        .title { font-size: 24px; font-weight: bold; margin-bottom: 10px; }
                        .highlight { color: #34d399; }
                    </style>
                </head>
                <body>
                    <div class="main-container">
                        <div class="title">Valinx - 今日签到</div>
                        <p>用户 ID: %d</p>
                        <p>获得奖励: <span class="highlight">+%.2f 云币</span></p>
                        <p>当前连签: %d 天</p>
                        <p>%s</p>
                    </div>
                </body>
                </html>
                """.formatted(result.uid(), result.reward(), result.streak(), result.message());
    }
}
