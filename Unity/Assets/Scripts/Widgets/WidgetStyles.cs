using UnityEngine;
using UnityEngine.UI;
using TMPro;

namespace HudLink.Widgets
{
    /// <summary>
    /// Shared visual constants and helper methods for consistent widget styling.
    /// All widgets should use these values to maintain a cohesive HUD appearance.
    /// </summary>
    public static class WidgetStyles
    {
        // Background
        public static readonly Color BgPrimary = new(0.08f, 0.08f, 0.12f, 0.85f);
        public static readonly Color BgHeader = new(0.12f, 0.12f, 0.18f, 0.9f);
        public static readonly float CornerRadius = 12f;

        // Text
        public static readonly Color TextPrimary = new(0.95f, 0.95f, 0.97f);
        public static readonly Color TextSecondary = new(0.6f, 0.63f, 0.7f);
        public static readonly Color TextMuted = new(0.4f, 0.42f, 0.48f);

        // Accent colors
        public static readonly Color AccentBlue = new(0.35f, 0.55f, 1f);
        public static readonly Color AccentGreen = new(0.3f, 0.85f, 0.5f);
        public static readonly Color AccentYellow = new(1f, 0.78f, 0.25f);
        public static readonly Color AccentRed = new(1f, 0.35f, 0.35f);
        public static readonly Color AccentCyan = new(0.3f, 0.85f, 0.9f);

        // Spacing
        public const float PaddingOuter = 12f;
        public const float PaddingInner = 6f;
        public const float HeaderHeight = 0.2f; // As fraction of widget height

        // Font sizes (scaled for world-space at 0.001 canvas scale)
        public const int FontSizeTitle = 14;
        public const int FontSizeValue = 42;
        public const int FontSizeUnit = 16;
        public const int FontSizeStatus = 11;

        /// <summary>
        /// Creates a styled background panel with optional border accent.
        /// </summary>
        public static Image CreateStyledBackground(Transform parent, Color bgColor, Color? borderColor = null)
        {
            // Main background
            var bgGo = new GameObject("Background");
            bgGo.transform.SetParent(parent, false);
            bgGo.transform.SetAsFirstSibling();

            var bgRect = bgGo.AddComponent<RectTransform>();
            bgRect.anchorMin = Vector2.zero;
            bgRect.anchorMax = Vector2.one;
            bgRect.offsetMin = Vector2.zero;
            bgRect.offsetMax = Vector2.zero;

            var bgImg = bgGo.AddComponent<Image>();
            bgImg.color = bgColor;

            // Top accent border
            if (borderColor.HasValue)
            {
                var borderGo = new GameObject("AccentBorder");
                borderGo.transform.SetParent(bgGo.transform, false);

                var borderRect = borderGo.AddComponent<RectTransform>();
                borderRect.anchorMin = new Vector2(0, 1f);
                borderRect.anchorMax = Vector2.one;
                borderRect.offsetMin = new Vector2(0, -3f);
                borderRect.offsetMax = Vector2.zero;

                var borderImg = borderGo.AddComponent<Image>();
                borderImg.color = borderColor.Value;
            }

            return bgImg;
        }

        /// <summary>
        /// Creates a header bar at the top of the widget with an icon label and title.
        /// </summary>
        public static TextMeshProUGUI CreateHeader(Transform parent, string icon, string title, Color accentColor)
        {
            var headerGo = new GameObject("Header");
            headerGo.transform.SetParent(parent, false);

            var headerRect = headerGo.AddComponent<RectTransform>();
            headerRect.anchorMin = new Vector2(0, 1f - HeaderHeight);
            headerRect.anchorMax = Vector2.one;
            headerRect.offsetMin = new Vector2(PaddingOuter, 0);
            headerRect.offsetMax = new Vector2(-PaddingOuter, 0);

            // Header background
            var headerBgGo = new GameObject("HeaderBg");
            headerBgGo.transform.SetParent(headerGo.transform, false);
            headerBgGo.transform.SetAsFirstSibling();
            var headerBgRect = headerBgGo.AddComponent<RectTransform>();
            headerBgRect.anchorMin = Vector2.zero;
            headerBgRect.anchorMax = Vector2.one;
            headerBgRect.offsetMin = new Vector2(-PaddingOuter, 0);
            headerBgRect.offsetMax = new Vector2(PaddingOuter, 0);
            var headerBgImg = headerBgGo.AddComponent<Image>();
            headerBgImg.color = BgHeader;

            var tmp = headerGo.AddComponent<TextMeshProUGUI>();
            tmp.text = $"{icon}  {title}";
            tmp.fontSize = FontSizeTitle;
            tmp.color = accentColor;
            tmp.alignment = TextAlignmentOptions.MidlineLeft;
            tmp.fontStyle = FontStyles.Bold;

            return tmp;
        }

        /// <summary>
        /// Creates a large value display in the center of the widget.
        /// </summary>
        public static TextMeshProUGUI CreateValueDisplay(Transform parent, string defaultText = "--")
        {
            var go = new GameObject("Value");
            go.transform.SetParent(parent, false);

            var rect = go.AddComponent<RectTransform>();
            rect.anchorMin = new Vector2(0, 0.2f);
            rect.anchorMax = new Vector2(0.7f, 1f - HeaderHeight);
            rect.offsetMin = new Vector2(PaddingOuter, 0);
            rect.offsetMax = new Vector2(0, -PaddingInner);

            var tmp = go.AddComponent<TextMeshProUGUI>();
            tmp.text = defaultText;
            tmp.fontSize = FontSizeValue;
            tmp.color = TextPrimary;
            tmp.alignment = TextAlignmentOptions.BottomLeft;
            tmp.fontStyle = FontStyles.Bold;

            return tmp;
        }

        /// <summary>
        /// Creates a unit label next to the value.
        /// </summary>
        public static TextMeshProUGUI CreateUnitLabel(Transform parent, string unit)
        {
            var go = new GameObject("Unit");
            go.transform.SetParent(parent, false);

            var rect = go.AddComponent<RectTransform>();
            rect.anchorMin = new Vector2(0.7f, 0.25f);
            rect.anchorMax = new Vector2(1f, 0.55f);
            rect.offsetMin = new Vector2(PaddingInner, 0);
            rect.offsetMax = new Vector2(-PaddingOuter, 0);

            var tmp = go.AddComponent<TextMeshProUGUI>();
            tmp.text = unit;
            tmp.fontSize = FontSizeUnit;
            tmp.color = TextSecondary;
            tmp.alignment = TextAlignmentOptions.BottomLeft;

            return tmp;
        }

        /// <summary>
        /// Creates a status bar at the bottom of the widget.
        /// </summary>
        public static TextMeshProUGUI CreateStatusBar(Transform parent, string defaultText = "")
        {
            var go = new GameObject("Status");
            go.transform.SetParent(parent, false);

            var rect = go.AddComponent<RectTransform>();
            rect.anchorMin = Vector2.zero;
            rect.anchorMax = new Vector2(1f, 0.2f);
            rect.offsetMin = new Vector2(PaddingOuter, PaddingInner);
            rect.offsetMax = new Vector2(-PaddingOuter, 0);

            var tmp = go.AddComponent<TextMeshProUGUI>();
            tmp.text = defaultText;
            tmp.fontSize = FontSizeStatus;
            tmp.color = TextMuted;
            tmp.alignment = TextAlignmentOptions.MidlineLeft;

            return tmp;
        }

        /// <summary>
        /// Creates a small indicator dot for connection/signal status.
        /// </summary>
        public static Image CreateStatusDot(Transform parent, Color color)
        {
            var go = new GameObject("StatusDot");
            go.transform.SetParent(parent, false);

            var rect = go.AddComponent<RectTransform>();
            rect.anchorMin = new Vector2(1f, 0f);
            rect.anchorMax = new Vector2(1f, 0.2f);
            rect.offsetMin = new Vector2(-PaddingOuter - 8f, PaddingInner + 2f);
            rect.offsetMax = new Vector2(-PaddingOuter, PaddingInner + 10f);
            rect.sizeDelta = new Vector2(8f, 8f);

            var img = go.AddComponent<Image>();
            img.color = color;

            return img;
        }
    }
}
