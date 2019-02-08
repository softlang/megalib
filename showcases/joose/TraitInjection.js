var Companies = require("./Structure");

var Confidence = Role("Confidence", {
    has: {
        selfConfidence: { is: "rw", init: 0.0 }
    },

    methods: {
        negotiate: function() {
            console.log("Boss we have to talk...");
            this.setWage(this.getWage() + this.getWage() * this.getSelfConfidence());
            console.log("Great.. i will accept");
        }
    }
});

var freelancer = new Freelancer({
    name: "Peter",
    wage: 50,
    hours: 160
});

// no negotiation possibile
console.log(freelancer.negotiate);

// learn to negotiate in runtime
freelancer.meta.extend({
    does: Confidence
});

// learned negotiation
console.log(freelancer.negotiate);

