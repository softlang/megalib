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

var ConfidentFreelancer = Class("ConfidentFreelancer", {
    isa: Companies.Freelancer,
    does: Confidence
});

var confidentFreelancer = new ConfidentFreelancer({
    name: "Peter",
    wage: 50,
    hours: 160,
    selfConfidence: 0.5
});

confidentFreelancer.report();
confidentFreelancer.negotiate();
confidentFreelancer.report();