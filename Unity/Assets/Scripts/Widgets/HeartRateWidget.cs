using UnityEngine;
using UnityEngine.UI;
using TMPro;

namespace HudLink.Widgets
{
    public class HeartRateWidget : BaseWidget
    {
        private TextMeshProUGUI _valueLabel;
        private TextMeshProUGUI _unitLabel;
        private TextMeshProUGUI _statusLabel;
        private Image _statusDot;
        private Image _bgPanel;

        private int _lastBpm;
        private float _lastUpdateTime;

        public override void Initialize(RectTransform slot)
        {
            base.Initialize(slot);

            _bgPanel = WidgetStyles.CreateStyledBackground(transform, WidgetStyles.BgPrimary, WidgetStyles.AccentRed);
            WidgetStyles.CreateHeader(transform, "\u2665", "HEART RATE", WidgetStyles.AccentRed);

            _valueLabel = WidgetStyles.CreateValueDisplay(transform);
            _unitLabel = WidgetStyles.CreateUnitLabel(transform, "BPM");
            _statusLabel = WidgetStyles.CreateStatusBar(transform, "Waiting for data...");
            _statusDot = WidgetStyles.CreateStatusDot(transform, WidgetStyles.TextMuted);
        }

        public override void UpdateData(WidgetData data)
        {
            if (data is not HeartRateWidgetData hrData) return;

            _lastUpdateTime = Time.time;

            if (!hrData.IsValid)
            {
                _valueLabel.text = "--";
                _valueLabel.color = WidgetStyles.TextMuted;
                _statusLabel.text = "No signal";
                _statusDot.color = WidgetStyles.AccentRed;
                return;
            }

            _lastBpm = hrData.Bpm;
            _valueLabel.text = _lastBpm.ToString();
            _valueLabel.color = GetHeartRateColor(_lastBpm);
            _statusLabel.text = "Live";
            _statusLabel.color = WidgetStyles.AccentGreen;
            _statusDot.color = WidgetStyles.AccentGreen;
        }

        private void Update()
        {
            if (!Initialized) return;

            float elapsed = Time.time - _lastUpdateTime;
            if (_lastUpdateTime > 0 && elapsed > 5f)
            {
                _statusLabel.text = $"{elapsed:F0}s ago";
                _statusLabel.color = elapsed > 10f ? WidgetStyles.AccentRed : WidgetStyles.TextMuted;
                _statusDot.color = elapsed > 10f ? WidgetStyles.AccentRed : WidgetStyles.AccentYellow;
            }
        }

        private Color GetHeartRateColor(int bpm)
        {
            if (bpm < 60) return WidgetStyles.AccentBlue;
            if (bpm < 100) return WidgetStyles.AccentGreen;
            if (bpm < 140) return WidgetStyles.AccentYellow;
            return WidgetStyles.AccentRed;
        }
    }
}
