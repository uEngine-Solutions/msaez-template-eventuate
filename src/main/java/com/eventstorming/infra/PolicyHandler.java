representativeFor: Policy
path: {{name}}/{{{options.packagePath}}}/infra
mergeType: template
---
package {{options.package}}.infra;

import javax.naming.NameParser;

import javax.naming.NameParser;
import javax.transaction.Transactional;

import {{options.package}}.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import {{options.package}}.domain.*;


@Service
@Transactional
@Configuration
public class PolicyHandler{
    {{#aggregates}}
    @Autowired {{namePascalCase}}Repository {{nameCamelCase}}Repository;
    {{/aggregates}}
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}

    {{#policies}}
    {{#relationAggregateInfo}}
    @Autowired
    {{../../options.package}}.external.{{aggregateValue.namePascalCase}}Service {{aggregateValue.nameCamelCase}}Service;

    {{/relationAggregateInfo}}


    {{#contexts.eventDispatchers}}

    @Bean
    public DomainEventDispatcher domainEventDispatcher(DomainEventDispatcherFactory domainEventDispatcherFactory) {
      return domainEventDispatcherFactory.make("{{aggregate.namePascalCase}}Events", DomainEventHandlersBuilder
      .forAggregateType("{{aggregate.namePascalCase}}")
      .onEvent({{eventAndPolicy.event.namePascalCase}}.class, PolicyHandler::whenever{{eventAndPolicy.event.namePascalCase}}_{{eventAndPolicy.policy.namePascalCase}})
      .build());
    }

    {{/contexts.eventDispatchers}}


    {{#relationEventInfo}}
    @StreamListener(value=KafkaProcessor.INPUT, condition="headers['type']=='{{eventValue.namePascalCase}}'")
    public void whenever{{eventValue.namePascalCase}}_{{../namePascalCase}}(@Payload {{eventValue.namePascalCase}} {{eventValue.nameCamelCase}}){

        {{eventValue.namePascalCase}} event = {{eventValue.nameCamelCase}};
        System.out.println("\n\n##### listener {{../namePascalCase}} : " + {{eventValue.nameCamelCase}} + "\n\n");

        {{#../relationAggregateInfo}}
        // REST Request Sample
        
        // {{aggregateValue.nameCamelCase}}Service.get{{aggregateValue.namePascalCase}}(/** mapping value needed */);

        {{/../relationAggregateInfo}}

        {{#todo ../description}}{{/todo}}

        // Sample Logic //
        {{#../aggregateList}}
        {{namePascalCase}}.{{../../nameCamelCase}}(event);
        
        {{/../aggregateList}}

        

    }
    {{/relationEventInfo}}

    {{/policies}}
}



<function>

var eventDispatchers = {};
policies.forEach(policy => {

    var aggergate = policy.relationEventInfo.eventValue.aggregate;
    var eventDispatcher = eventDispatchers[aggergate.name];

    if(!eventDispatcher){
        eventDispatcher = {
            aggregate: aggregate,
            eventAndPolicy: []
        };

        eventDispatchers[aggergate.name] = eventDispatcher;
    }

    eventDispatcher.eventAndPolicy.push({event: policy.relationEventInfo.eventValue, policy: policy});
});

contexts["eventDispatchers"] = eventDispatchers;


window.$HandleBars.registerHelper('todo', function (description) {

    if(description){
        description = description.replaceAll('\n','\n\t\t// ')
        return description = '// Comments // \n\t\t//' + description;
    }
     return null;
});

window.$HandleBars.registerHelper('log', function (msg) {

    console.log("log for template", msg);
     return null;
});

</function>