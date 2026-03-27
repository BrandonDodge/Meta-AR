using UnityEngine;
using UnityEngine.UI;
using TMPro;

namespace HudLink.Widgets
{
    public class GPSWidget : BaseWidget
    {
        private TextMeshProUGUI _speedLabel;
        private TextMeshProUGUI _unitLabel;
        private TextMeshProUGUI _headingLabel;
        private TextMeshProUGUI _statusLabel;
        private Image _statusDot;

        private static readonly string[] CardinalDirections =
            { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };

        public override void Initialize(RectTransform slot)
        {
            base.Initialize(slot);

            WidgetStyles.CreateStyledBackground(transform, WidgetStyles.BgPrimary, WidgetStyles.AccentCyan);
            WidgetStyles.CreateHeader(transform, "\u2316", "LOCATION", WidgetStyles.AccentCyan);

            _speedLabel = WidgetStyles.CreateValueDisplay(transform);
            _unitLabel = WidgetStyles.CreateUnitLabel(transform, "MPH");

            var headingGo = new GameObject("Heading");
            headingGo.transform.SetParent(transform, false);
            var headingRect = headingGo.AddComponent<RectTransform>();
            headingRect.anchorMin = new Vector2(0.55f, 0.55f);
            headingRect.anchorMax = new Vector2(1f, 0.8f);
            headingRect.offsetMin = new Vector2(0, 0);
            headingRect.offsetMax = new Vector2(-WidgetStyles.PaddingOuter, 0);
            _headingLabel = headingGo.AddComponent<TextMeshProUGUI>();
            _headingLabel.text = "--";
            _headingLabel.fontSize = 18;
            _headingLabel.color = WidgetStyles.TextSecondary;
            _headingLabel.alignment = TextAlignmentOptions.MidlineRight;

            _statusLabel = WidgetStyles.CreateStatusBar(transform, "No GPS");
            _statusDot = WidgetStyles.CreateStatusDot(transform, WidgetStyles.TextMuted);
        }

        public override void UpdateData(WidgetData data)
        {
            if (data is not GpsWidgetData gpsData) return;

            if (!gpsData.HasFix)
            {
                _speedLabel.text = "--";
                _speedLabel.color = WidgetStyles.TextMuted;
                _headingLabel.text = "--";
                _statusLabel.text = "Acquiring signal...";
                _statusLabel.color = WidgetStyles.AccentYellow;
                _statusDot.color = WidgetStyles.AccentYellow;
                return;
            }

            _speedLabel.text = $"{gpsData.SpeedMph:F1}";
            _speedLabel.color = WidgetStyles.TextPrimary;
            _headingLabel.text = DegreesToCardinal(gpsData.HeadingDegrees);
            _headingLabel.color = WidgetStyles.AccentCyan;
            _statusLabel.text = "GPS Active";
            _statusLabel.color = WidgetStyles.AccentGreen;
            _statusDot.color = WidgetStyles.AccentGreen;
        }

        private string DegreesToCardinal(float degrees)
        {
            int index = Mathf.RoundToInt(degrees / 45f) % 8;
            if (index < 0) index += 8;
            return $"{CardinalDirections[index]}  {degrees:F0}\u00b0";
        }
    }
}
