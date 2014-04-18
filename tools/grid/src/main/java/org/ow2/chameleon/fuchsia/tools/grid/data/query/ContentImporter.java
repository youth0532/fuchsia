/*
 * #%L
 * OW2 Chameleon - Fuchsia Framework
 * %%
 * Copyright (C) 2009 - 2014 OW2 Chameleon
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.ow2.chameleon.fuchsia.tools.grid.data.query;

import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.ow2.chameleon.fuchsia.core.component.ImportationLinkerIntrospection;
import org.ow2.chameleon.fuchsia.core.component.ImporterService;
import org.ow2.chameleon.fuchsia.tools.grid.ContentHelper;
import org.ow2.chameleon.fuchsia.tools.grid.model.ImportationLinkerNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Instantiate
public class ContentImporter extends HttpServlet {

    @Requires
    HttpService web;

    final String URL="/contentImporter";

    @Requires
    ContentHelper content;

    BundleContext context;

    public ContentImporter(BundleContext context){
        this.context=context;
    }

    @Validate
    public void validate(){
        try {
            web.registerServlet(URL,this,null,null);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (NamespaceException e) {
            e.printStackTrace();
        }
    }

    @Invalidate
    public void invalidate(){
        web.unregister(URL);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<ImportationLinkerNode> rootList=new ArrayList<ImportationLinkerNode>();

        ObjectMapper mapper=new ObjectMapper();

        List<String> ifaces=new ArrayList<String>(){{add(ImporterService.class.getName());}};
        List<String> props=new ArrayList<String>(){{}};

        for(Factory factory:content.getFuchsiaFactories(ifaces,props)){
            rootList.add(new ImportationLinkerNode(factory.getName()));
        }


        /*
        try {

            ServiceReference[] references=context.getServiceReferences(Factory.class.getName(),null);

            if(references!=null) {
                for (ServiceReference sr : references) {

                    for(String key:sr.getPropertyKeys()){
                        System.out.println(key+"----->" + sr.getProperty(key));
                    }

                    System.out.println("######################################");
                      
                    Factory fact=(Factory) context.getService(sr);

                }
            }

        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        } */

        //rootList.add(new ImportationLinkerNode("jander fact"));

        mapper.writeValue(resp.getWriter(),rootList);

    }

}
