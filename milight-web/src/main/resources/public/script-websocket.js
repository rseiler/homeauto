(function ($) {
    "use strict";

    var socket = new WebSocket(window.location.href.replace('http', 'ws') + 'ws');
    socket.onopen = function () {
        console.log("Socket has been opened!");
    };
    socket.onerror = function (e) {
        alert('ws error: ' + JSON.stringify(e));
    };

    var lastValue;

    function makeCommandFunc(command) {
        return function (value) {
            if (!value || value != lastValue) {

                if ($('#group-all').prop("checked")) {
                    socket.send(command + (value ? '/' + value : ''));
                } else {
                    var groups = '';

                    for (var i = 1; i < 5; i++) {
                        if ($('#group-' + i).prop("checked")) {
                            groups += String(i);
                        }
                    }

                    socket.send(command + (value ? '/' + value : '') + '/' + groups);
                }
            }

            lastValue = value;
        };
    }

    window.milight = {
        on: makeCommandFunc('on'),
        off: makeCommandFunc('off'),
        white: makeCommandFunc('white'),
        night: makeCommandFunc('night'),
        discoMode: makeCommandFunc('discoMode'),
        discoModeFaster: makeCommandFunc('discoModeFaster'),
        discoModeSlower: makeCommandFunc('discoModeSlower'),
        color: makeCommandFunc('color'),
        brightness: makeCommandFunc('brightness')
    };

    $(document).ready(function ($) {
        $('#color-picker').spectrum({
            flat: true,
            showButtons: false,
            color: '#ff0',
            move: function (color) {
                var miLightColor = parseInt(color.toHsv().h / 360.0 * 255.0);
                miLightColor = 0xff - miLightColor + 176;

                if (miLightColor > 255) {
                    miLightColor -= 255;
                }

                if (miLightColor < 0) {
                    miLightColor += 255;
                }

                window.milight.color(miLightColor);
            }
        });

        $(function () {
            $("#brightness-picker").slider({
                orientation: "vertical",
                range: "min",
                min: 2,
                max: 27,
                value: 13,
                slide: function (event, ui) {
                    window.milight.brightness(ui.value);
                }
            });
        });
    });
})(jQuery);
