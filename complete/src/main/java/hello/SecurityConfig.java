/*
 *
 * Copyright (c) 2017 CA Technologies, Inc.  All rights reserved.
 *
 * This software and all information contained therein is confidential and proprietary
 * and shall not be duplicated, used, disclosed or disseminated in any way except as
 * authorized by the applicable license agreement, without the express written
 * permission of CA Technologies, Inc. All authorized reproductions must be marked
 * with this language.
 *
 * EXCEPT AS SET FORTH IN THE APPLICABLE LICENSE AGREEMENT, TO THE EXTENT
 * PERMITTED BY APPLICABLE LAW OR AS AGREED BY CA TECHNOLOGIES, INC. IN ITS
 * APPLICABLE LICENSE AGREEMENT, CA TECHNOLOGIES, INC. PROVIDES THIS SOFTWARE
 * "AS IS" WITHOUT WARRANTY OF ANY KIND, INCLUDING WITHOUT LIMITATION, ANY
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NONINFRINGEMENT. IN NO EVENT WILL CA TECHNOLOGIES, INC.  BE LIABLE TO
 * THE END USER OR ANY THIRD PARTY FOR ANY LOSS OR DAMAGE, DIRECT OR
 * INDIRECT, FROM THE USE OF THIS SOFTWARE, INCLUDING WITHOUT LIMITATION,
 * LOST PROFITS, LOST INVESTMENT, BUSINESS INTERRUPTION, GOODWILL, OR LOST
 * DATA, EVEN IF CA TECHNOLOGIES, INC. IS EXPRESSLY ADVISED IN ADVANCE OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 */

package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Created by wbeckwith.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
            .withUser("admin").password("password").roles("USER", "ADMIN").and()
            .withUser("foo").password("bar").roles("USER");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .httpBasic().and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/**").hasRole("USER")
            .anyRequest().authenticated();
    }
}
