/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.templates.service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zols.datastore.DataStore;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zols.datastore.query.Filter;
import static org.zols.datastore.query.Filter.Operator.EQUALS;
import org.zols.datastore.query.Query;
import org.zols.datatore.exception.DataStoreException;
import org.zols.templates.domain.Template;
import org.zols.templates.domain.TemplateRepository;

/**
 *
 * @author sathish_ku
 */
@Service
public class TemplateRepositoryService {

    private static final Logger LOGGER = getLogger(TemplateRepositoryService.class);

    @Autowired
    private DataStore dataStore;

    /**
     * Creates a new TemplateRepository with given Object
     *
     * @param templateRepository Object to be Create
     * @return created TemplateRepository object
     */
    public TemplateRepository create(TemplateRepository templateRepository) throws DataStoreException {
        TemplateRepository createdTemplateRepository = null;
        if (templateRepository != null) {
            createdTemplateRepository = dataStore.create(templateRepository);
            LOGGER.info("Created Template Repository {}", createdTemplateRepository.getName());
        }
        return createdTemplateRepository;
    }

    /**
     * Get the TemplateRepository with given String
     *
     * @param templateRepositoryName String to be Search
     * @return searched TemplateRepository
     */
    public TemplateRepository read(String templateRepositoryName) throws DataStoreException {
        LOGGER.info("Reading Template Repository {}", templateRepositoryName);
        return dataStore.read(TemplateRepository.class, templateRepositoryName);
    }

    /**
     * Update a TemplateRepository with given Object
     *
     * @param templateRepository Object to be update
     * @return status of the Update Operation
     */
    public TemplateRepository update(TemplateRepository templateRepository) throws DataStoreException {
        TemplateRepository updated = null;
        if (templateRepository != null) {
            LOGGER.info("Updating Template Repository {}", templateRepository);
            updated = dataStore.update(templateRepository, templateRepository.getName());
        }
        return updated;
    }

    /**
     * Delete a TemplateRepository with given String
     *
     * @param templateRepositoryName String to be delete
     * @return status of the Delete Operation
     */
    public Boolean delete(String templateRepositoryName) throws DataStoreException {
        LOGGER.info("Deleting Template Repository {}", templateRepositoryName);
        dataStore.delete(TemplateRepository.class, templateRepositoryName);
        return deleteTemplatesUnder(templateRepositoryName);
    }

    /**
     * Delete a Templates under given repository
     *
     * @param templateRepositoryName String to be delete
     * @return status of the Delete Operation
     */
    public Boolean deleteTemplatesUnder(String templateRepositoryName) throws DataStoreException {
        LOGGER.info("Deleting Template  under Repository {}", templateRepositoryName);
        Query query = new Query();
        query.addFilter(new Filter("repositoryName", Filter.Operator.EQUALS, templateRepositoryName));
        return dataStore.delete(Template.class, query);
    }

    /**
     * List all TemplateRepositories
     *
     * @return list of all Template Repositories
     */
    public List<TemplateRepository> list() throws DataStoreException {
        LOGGER.info("Getting TemplateRepositories ");
        return dataStore.list(TemplateRepository.class);
    }

    /**
     * List templates under given repository
     *
     * @param repositoryName name of the repository
     * @return list of templates
     */
    public List<Template> listTemplates(String repositoryName) throws DataStoreException {
        LOGGER.info("Getting templates of repository  {}", repositoryName);
        Query query = new Query();
        query.addFilter(new Filter<>("repositoryName", EQUALS, repositoryName));
        return dataStore.list(Template.class, query);
    }

    /**
     * List templates under given repository
     *
     * @param repositoryName name of the repository
     * @return list of templates
     */
    public List<Map<String, String>> listTemplateFiles(String repositoryName) throws DataStoreException {
        List<Map<String, String>> templateFiles = null;
        TemplateRepository repository = read(repositoryName);
        if (repository != null) {
            if (repository.getType().equals(TemplateRepository.FILE_SYSTEM)) {
                templateFiles = new ArrayList<>();
                File root = new File(repository.getPath());
                listTemplateFiles(templateFiles, root.getAbsolutePath(), root);

            }
        }
        return templateFiles;
    }

    private void listTemplateFiles(List<Map<String, String>> templateFiles, String rootPath, File folder) {
        File[] files = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || pathname.getName().endsWith(".html");
            }
        });

        for (File file : files) {
            if (file.isDirectory()) {
                listTemplateFiles(templateFiles, rootPath, file);
            } else {
                Map<String, String> map = new HashMap<>(1);
                String filePath = file.getAbsolutePath().substring(rootPath.length());
                map.put("label", filePath);
                map.put("value", filePath.replaceAll(".html", ""));
                templateFiles.add(map);
            }
        }

    }
}
