import os
import subprocess as sp
from os import listdir
from os.path import isfile, join
from concurrent.futures import ProcessPoolExecutor
import uuid
import logging

STREAMING_LOG_DIRECTORY = os.environ['STREAMING_LOG_DIRECTORY']
BATCH_LOG_DIRECTORY = os.environ['BATCH_LOG_DIRECTORY']
BATCH_CLIENT_APPS_DIRECTORY = os.environ['BATCH_CLIENT_APPS_DIRECTORY']
STREAMING_CLIENT_APPS_DIRECTORY = os.environ['STREAMING_CLIENT_APPS_DIRECTORY']
MAX_WORKERS = int(os.environ['MAX_WORKERS'])
BATCH_EXECUTOR = dict()         # ProcessPoolExecutor(max_workers=MAX_WORKERS)
STREAMING_EXECUTOR = dict()     # ProcessPoolExecutor(max_workers=MAX_WORKERS)


def run_jar_async(jar_path, jar_file_name, log_directory, executor):
    print("Intending to run: ", jar_file_name)
    file_name_and_uuid = jar_file_name + "-" + uuid.uuid4().hex
    proc = run_jar(jar_path, file_name_and_uuid, log_directory)
    executor['jars'].append(proc)
    return file_name_and_uuid


def run_jar(jar_path, jar_file_name_and_uuid, log_directory):
    with open(log_directory + jar_file_name_and_uuid + ".log", "w") as log_file:
        proc = sp.Popen(['java', '-jar', jar_path], stdout=log_file, stderr=sp.STDOUT)
        logging.info("Processing of ", jar_file_name_and_uuid)     # TODO this sout is ignored. Use logger.
        # exit_code = proc.wait()
        return proc


def run_all_batch(customer_name):
    if BATCH_EXECUTOR.get(customer_name, None) is None:
        BATCH_EXECUTOR[customer_name] = {'jars': []}
    return run_all(BATCH_CLIENT_APPS_DIRECTORY, customer_name, BATCH_LOG_DIRECTORY, BATCH_EXECUTOR[customer_name])


def run_all_streaming(customer_name):
    if STREAMING_EXECUTOR.get(customer_name, None) is None:
        STREAMING_EXECUTOR[customer_name] = {'jars': []}
    return run_all(STREAMING_CLIENT_APPS_DIRECTORY, customer_name, STREAMING_LOG_DIRECTORY, STREAMING_EXECUTOR[customer_name])


def run_all(directory_path, customer_name, log_directory, executor):
    files = [f for f in listdir(directory_path) if isfile(join(directory_path, f))]
    jar_files = [file for file in files if file.endswith(".jar") and file.startswith(customer_name)]
    log_files = [run_jar_async(join(directory_path, file), file, log_directory, executor) for file in jar_files]
    return log_files


def kill_all_batch(customer_name):
    [p.kill() for p in BATCH_EXECUTOR[customer_name]['jars']]


def kill_all_streaming(customer_name):
    [p.kill() for p in STREAMING_EXECUTOR[customer_name]['jars']]
