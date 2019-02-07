var Class = require("joose").Class;

var EventEmitter    = require("events").EventEmitter;

var NiceClass = Class("Metaclass", {
    meta: Joose.Meta.Class,
    isa: EventEmitter,
    before: {
        emit: function(event) {
            console.log("Emitting event [" + event + "]")
        }
    }
});

var inst = new NiceClass();
inst.emit("HELLO")
