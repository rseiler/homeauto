(function ($) {
    "use strict";

    function push(array, value) {
        if (!!value) {
            array.push(value);
        }

        return array;
    }

    function sendRequest(command, value) {
        if ($('#group-all').prop("checked")) {
            $.ajax({
                url: 'exec',
                data: JSON.stringify(push([command], value)),
                type: 'PUT',
                dataType: "json",
                contentType: "application/json; charset=utf-8"
            });
        } else {
            for (var i = 1; i < 5; i++) {
                if ($('#group-' + i).prop("checked")) {
                    $.ajax({
                        url: 'exec',
                        data: JSON.stringify(push([command, i], value)),
                        type: 'PUT',
                        dataType: "json",
                        contentType: "application/json; charset=utf-8"
                    });
                }
            }
        }
    }

    var lastValue;

    function makeCommandFunc(command) {
        return function (value) {
            if (!value || value !== lastValue) {
                sendRequest(command, value);
                lastValue = value;
            }
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

        function makeShortcutFunc(group) {
            return function () {
                $('#group-' + group).prop("checked", "checked");
            };
        }

        for (var i = 1; i < 5; i++) {
            $(document).bind('keydown', 'alt+' + i, makeShortcutFunc(i));
        }

        $(document).bind('keydown', 'alt+a', makeShortcutFunc('all'));
        $(document).bind('keydown', 'alt+q', function (e) {
            e.preventDefault();
            sendRequest('off');
        });
        $(document).bind('keydown', 'alt+w', function (e) {
            e.preventDefault();
            sendRequest('white');
        });
        $(document).bind('keydown', 'alt+e', function (e) {
            e.preventDefault();
            sendRequest('on');
        });
    });
})(jQuery);
